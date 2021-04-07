package com.example.mtaafe.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.repositories.AuthRepository
import com.example.mtaafe.utils.PropertiesReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginViewModel: ViewModel() {
    private var authRepository:AuthRepository?=null

    private var _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
            get() = _error

    init {
        authRepository = AuthRepository()
    }

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val credentials = Credentials("jozefmrkva@stuba.sk", "password1234")
            val response = authRepository?.login(credentials)

            Log.i("Login response", response.toString())
            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }

                    is ApiResult.Success -> {
                        // do something with success response
                    }
                }
            }
        }
    }
}