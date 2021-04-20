package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.QuestionsList
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionDetailViewModel(application: Application): AndroidViewModel(application) {

    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    fun getQuestionDetails(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionDetails(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun retry(questionId: Long) {
        getQuestionDetails(questionId)
    }
}