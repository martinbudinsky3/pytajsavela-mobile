package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.repositories.AnswersRepository
import com.example.mtaafe.utils.SessionManager
import com.example.mtaafe.views.activities.AnswerEdit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnswerEditViewModel(application: Application): AndroidViewModel(application) {

    private var answersRepository: AnswersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        answersRepository = AnswersRepository()
        sessionManager = SessionManager(application)
    }

    fun getAnswerEditForm(answerId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = answersRepository?.getAnswerEditForm(sessionManager?.fetchApiToken().toString(), answerId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun retry(answerId: Long) {
        getAnswerEditForm(answerId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editAnswer(answerEdit: AnswerEdit){
        CoroutineScope(Dispatchers.IO).launch {

            val response = answersRepository?.editAnswer(
                    sessionManager?.fetchApiToken().toString(),
                    answerEdit
            )

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }
}