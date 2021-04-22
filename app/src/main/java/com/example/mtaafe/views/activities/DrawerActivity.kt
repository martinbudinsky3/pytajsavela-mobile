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
import com.example.mtaafe.R
import com.example.mtaafe.utils.SessionManager
import com.google.android.material.navigation.NavigationView

open class DrawerActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    protected lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_activity)

        sessionManager = SessionManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        val navigationView: NavigationView = findViewById(R.id.navigation)
        navigationView.setNavigationItemSelectedListener(this)
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
                sessionManager.logout()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}