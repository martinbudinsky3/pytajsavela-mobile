package com.example.mtaafe.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.databinding.ActivityMainBinding
import com.example.mtaafe.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private  lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}