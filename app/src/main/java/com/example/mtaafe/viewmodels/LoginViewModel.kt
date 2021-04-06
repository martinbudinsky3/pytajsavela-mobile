package com.example.mtaafe.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginViewModel: ViewModel() {
    private var authRepository:AuthRepository?=null

    init {
        authRepository = AuthRepository()
    }

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val credentials = Credentials("jozefmrkva@stuba.sk", "password123")
            val response = authRepository?.login(credentials)
            withContext(Dispatchers.Main) {
                try {
                    if (response?.isSuccessful == true) {
                        Log.d("login test debug", response.body().toString())
                    } else {
                        Log.e("login not success", response.toString())
                    }
                } catch (e: HttpException) {
                    Log.e("login not success", "Http exception", e)
                } catch (e: Throwable) {
                    Log.e("login not success", "Generic exception", e)
                }
            }
        }
    }
}