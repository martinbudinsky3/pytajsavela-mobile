package com.example.mtaafe.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sessionManager = SessionManager(this)

        fun handleError(error: ErrorEntity) {
            when(error) {
                is ErrorEntity.Unauthorized -> Toast.makeText(this, "Nesprávne prihlasovacie údaje", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Oops, niečo sa pokazilo. Vyskúšajte akciu neskôr prosím", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is LoggedInUser) {
                        sessionManager.saveApiToken(it.data.apiToken)
                        sessionManager.saveUserId(it.data.id)
                        val intent = Intent(this, QuestionsListActivity::class.java)
                        startActivity(intent)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })
    }
}