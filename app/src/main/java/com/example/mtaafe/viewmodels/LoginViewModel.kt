package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
class LoginViewModel: ViewModel() {
    private var authRepository:AuthRepository?=null

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        authRepository = AuthRepository()
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