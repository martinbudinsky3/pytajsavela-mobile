package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.repositories.AuthRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel: ViewModel() {
    private var authRepository:AuthRepository?=null

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loggedInUser = MutableLiveData<LoggedInUser>()
    val loggedInUser: LiveData<LoggedInUser>
        get() = _loggedInUser

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    private var fcmToken: String? = null

    init {
        authRepository = AuthRepository()
    }

    fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val credentials = Credentials(email.value.toString(), password.value.toString())
            val response = authRepository?.login(credentials)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is LoggedInUser) {
                            _loggedInUser.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }
                }
            }
        }
    }

    fun postFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Fcm token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Log.d("Token", task.result!!)
            // Get new FCM registration token
            fcmToken = task.result
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("Token", "Sending token")
                val fcmTokenData = FcmToken(fcmToken!!)
                authRepository?.postFcmToken(_loggedInUser.value!!.apiToken, fcmTokenData)
            }
        })
    }
}