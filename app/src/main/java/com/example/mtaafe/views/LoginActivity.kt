package com.example.mtaafe.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.databinding.ActivityLoginBinding
import com.example.mtaafe.utils.SessionManager
import com.example.mtaafe.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var loginRoot: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //supportActionBar?.hide()
        loginRoot = findViewById(R.id.loginRoot)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sessionManager = SessionManager(this)

        viewModel.loggedInUser.observe(this, {
            sessionManager.saveApiToken(it.apiToken)
            sessionManager.saveUserId(it.id)
            val intent = Intent(this, QuestionsListActivity::class.java)
            startActivity(intent)
        })

        viewModel.error.observe(this, {
            handleError(it)
        })
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                Snackbar.make(loginRoot, "Nesprávne prihlasovacie údaje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") {}
                    .show()
            }
            else -> {
                Snackbar.make(loginRoot, "Oops, niečo sa pokazilo", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.login()
                    }
                    .show()
            }
        }
    }
}