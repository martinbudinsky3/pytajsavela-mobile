package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.models.QuestionsList
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.ceil

class QuestionsListViewModel(application: Application): AndroidViewModel(application) {
    private val PAGE_SIZE: Int = 10

    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null
    private var currentPage: Int = 1;
    private var count: Long = 0

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    private fun getQuestionsList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionsList(sessionManager?.fetchApiToken().toString(), page)

            withContext(Dispatchers.Main) {
                _result.value = response!!

                if(response is ApiResult.Success) {
                    currentPage = page

                    if(response.data is QuestionsList) {
                        count = response.data.count
                    }
                }
            }
        }
    }

    fun getFirstPage() {
        getQuestionsList(1)
    }

    fun getPrevioustPage() {
        getQuestionsList(currentPage - 1)
    }

    fun getNextPage() {
        Log.d("Questions list api call", (currentPage + 1).toString())
        getQuestionsList(currentPage + 1)
    }

    fun getLastPage() {
        var lastPage = count / PAGE_SIZE
        if(count % PAGE_SIZE != 0L) {
            lastPage++
        }
        getQuestionsList(lastPage.toInt())
    }

    fun retry() {
        getQuestionsList(currentPage)
    }
}