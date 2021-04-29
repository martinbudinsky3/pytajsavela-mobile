package com.example.mtaafe.views.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.notifications.FirebaseSessionManager
import com.example.mtaafe.utils.SessionManager
import com.example.mtaafe.viewmodels.DrawerViewModel
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

open class DrawerActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: DrawerViewModel
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        val navigationView: NavigationView = findViewById(R.id.navigation)
        navigationView.setNavigationItemSelectedListener(this)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(DrawerViewModel::class.java)

        viewModel.successfulLogout.observe(this, {
            if(it == true) {
                FirebaseSessionManager.disableFCM()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.error.observe(this, {
            handleError(it)
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_menu_item -> {
                if(this is UserProfileActivity) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.questions_menu_item -> {
                if(this is QuestionsListActivity) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    val intent = Intent(this, QuestionsListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.tags_menu_item -> {
                if(this is TagsListActivity) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    val intent = Intent(this, TagsListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.logout_menu_item -> {
                viewModel.logout()
            }
        }
        return true
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else -> {
                Snackbar.make(
                    drawer,
                    "Nepodarilo sa odhlásiť",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Skúsiť znovu") {
                        viewModel.logout()
                    }
                    .show()
            }
        }
    }
}