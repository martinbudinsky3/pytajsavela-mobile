package com.example.mtaafe.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.databinding.ActivityMainBinding
import com.example.mtaafe.utils.PropertiesReader
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

        viewModel.error.observe(this, Observer {
            when(it) {
                is ErrorEntity.Unauthorized -> Toast.makeText(this, "Nesprávne prihlasovacie údaje", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Oops, niečo sa pokazilo. Vyskúšajte akciu neskôr prosím", Toast.LENGTH_LONG).show()
            }
        })
    }
}