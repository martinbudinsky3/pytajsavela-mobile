package com.example.mtaafe.viewmodels

import android.app.Application
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

class UserProfileViewModel(application: Application): AndroidViewModel(application)  {
    private var usersRepository: UsersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _errorInfo = MutableLiveData<ErrorEntity>()
    val errorInfo: LiveData<ErrorEntity>
        get() = _errorInfo

    private val _errorQuestions = MutableLiveData<ErrorEntity>()
    val errorQuestions: LiveData<ErrorEntity>
        get() = _errorQuestions

    private val _errorAnswers = MutableLiveData<ErrorEntity>()
    val errorAnswers: LiveData<ErrorEntity>
        get() = _errorAnswers

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userQuestionsList = MutableLiveData<UserQuestionsList>()
    val userQuestionsList: LiveData<UserQuestionsList>
        get() = _userQuestionsList

    private val _userAnswersList = MutableLiveData<UserAnswersList>()
    val userAnswersList: LiveData<UserAnswersList>
        get() = _userAnswersList

    private var userId: Long = 1

    init {
        usersRepository = UsersRepository()
        sessionManager = SessionManager(application)
    }

    fun setUserId(userId: Long) {
        this.userId = userId
    }

    fun setLoggedInUserId() {
        this.userId = sessionManager?.fetchUserId()!!
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
                        _errorInfo.value = response.error
                    }
                }
            }
        }
    }

    fun getUserQuestionsList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usersRepository?.getUserQuestionsList(sessionManager?.fetchApiToken().toString(), userId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is UserQuestionsList) {
                            _userQuestionsList.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _errorQuestions.value = response.error
                    }
                }
            }
        }
    }

    fun getUserAnswersList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usersRepository?.getUserAnswersList(sessionManager?.fetchApiToken().toString(), userId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is UserAnswersList) {
                            _userAnswersList.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _errorAnswers.value = response.error
                    }
                }
            }
        }
    }
}