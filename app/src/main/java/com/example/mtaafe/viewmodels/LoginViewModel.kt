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

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

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
                _result.value = response!!
            }
        }
    }
}