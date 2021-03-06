package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.User
import com.example.mtaafe.data.repositories.UsersRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoViewModel(application: Application): AndroidViewModel(application)  {
    private var usersRepository: UsersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    var userId: Long = 1

    init {
        usersRepository = UsersRepository()
        sessionManager = SessionManager(application)
    }

    fun getUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usersRepository?.getUserInfo(sessionManager?.fetchApiToken().toString(), userId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is User) {
                            _user.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }
                }
            }
        }
    }
}