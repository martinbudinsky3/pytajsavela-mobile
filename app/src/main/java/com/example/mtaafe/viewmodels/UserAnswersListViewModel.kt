package com.example.mtaafe.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.UserAnswersList
import com.example.mtaafe.data.models.UserQuestionsList
import com.example.mtaafe.data.repositories.UsersRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAnswersListViewModel (application: Application): AndroidViewModel(application) {
    private var usersRepository: UsersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _userAnswersList = MutableLiveData<UserAnswersList>()
    val userAnswersList: LiveData<UserAnswersList>
        get() = _userAnswersList

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    var userId: Long = 3

    init {
        usersRepository = UsersRepository()
        sessionManager = SessionManager(application)
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
                        _error.value = response.error
                    }
                }
            }
        }
    }
}