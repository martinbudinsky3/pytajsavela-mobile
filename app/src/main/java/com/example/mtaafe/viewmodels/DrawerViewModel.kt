package com.example.mtaafe.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.QuestionsList
import com.example.mtaafe.data.repositories.AuthRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawerViewModel(application: Application): AndroidViewModel(application) {
    private var authRepository: AuthRepository? = null
    private var sessionManager: SessionManager? = null

    private val _successfulLogout = MutableLiveData<Boolean>()
    val successfulLogout: LiveData<Boolean>
        get() = _successfulLogout

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    init {
        authRepository = AuthRepository()
        sessionManager = SessionManager(application)
    }

    fun logout() {
        Log.d("Token", sessionManager?.fetchApiToken().toString())
        CoroutineScope(Dispatchers.IO).launch {
            val response = authRepository?.logout(sessionManager?.fetchApiToken().toString())

            withContext(Dispatchers.Main) {

                when(response) {
                    is ApiResult.Success -> {
                        sessionManager?.logout()
                        _successfulLogout.value = true
                    }
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }
                }
            }
        }
    }
}