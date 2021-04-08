package com.example.mtaafe.views

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
import com.example.mtaafe.databinding.ActivityMainBinding
import com.example.mtaafe.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(LoginViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        

        fun handleError(error: ErrorEntity) {
            when(error) {
                is ErrorEntity.Unauthorized -> Toast.makeText(this, "Nesprávne prihlasovacie údaje", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Oops, niečo sa pokazilo. Vyskúšajte akciu neskôr prosím", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> { /* handle success */ }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })
    }
}