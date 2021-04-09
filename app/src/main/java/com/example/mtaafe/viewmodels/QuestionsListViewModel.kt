package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsListViewModel(application: Application): AndroidViewModel(application) {
    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null

    val questions = MutableLiveData<String>()
    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    fun getQuestionsList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionsList(sessionManager?.fetchApiToken().toString())

            withContext(Dispatchers.Main) {
                questions.value = response.toString()
                _result.value = response!!
            }
        }
    }
}