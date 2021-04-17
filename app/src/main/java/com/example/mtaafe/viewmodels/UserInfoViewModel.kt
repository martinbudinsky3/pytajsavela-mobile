package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.repositories.UsersRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoViewModel(application: Application): AndroidViewModel(application)  {
    private var usersRepository: UsersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    var userId: Long = 1

    init {
        usersRepository = UsersRepository()
        sessionManager = SessionManager(application)
    }

    fun getUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usersRepository?.getUserInfo(sessionManager?.fetchApiToken().toString(), userId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }
}