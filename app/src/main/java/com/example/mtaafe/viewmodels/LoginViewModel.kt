package com.example.mtaafe.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.LoggedInUser
import com.example.mtaafe.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(application: Application): AndroidViewModel(application) {
    private var authRepository:AuthRepository?=null
    private var sharedPreferences: SharedPreferences

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
            get() = _error

    init {
        authRepository = AuthRepository()
        sharedPreferences = getApplication<Application>()
                .getSharedPreferences("user_info", Context.MODE_PRIVATE)
    }

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val credentials = Credentials(email.value.toString(), password.value.toString())
            val response = authRepository?.login(credentials)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }

                    is ApiResult.Success -> {
                        if(response.data is LoggedInUser) {
                            sharedPreferences.edit().putString("api_token", response.data.apiToken).apply()
                            sharedPreferences.edit().putLong("id", response.data.id).apply()
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}