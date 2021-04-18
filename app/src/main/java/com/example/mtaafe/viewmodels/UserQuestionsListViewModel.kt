package com.example.mtaafe.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.repositories.UsersRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserQuestionsListViewModel(application: Application): AndroidViewModel(application) {
    private var usersRepository: UsersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _userQuestionsList = MutableLiveData<UserQuestionsList>()
    val userQuestionsList: LiveData<UserQuestionsList>
        get() = _userQuestionsList

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    var userId: Long = 1

    init {
        usersRepository = UsersRepository()
        sessionManager = SessionManager(application)
    }

    fun getUserQuestionsList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usersRepository?.getUserQuestionsList(sessionManager?.fetchApiToken().toString(), userId)

            Log.d("User questions response", response.toString())
            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is UserQuestionsList) {
                            _userQuestionsList.value = response.data!!
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