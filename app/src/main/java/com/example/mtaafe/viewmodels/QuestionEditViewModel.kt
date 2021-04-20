package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.QuestionEdit
import com.example.mtaafe.data.models.Tag
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class QuestionEditViewModel(application: Application): AndroidViewModel(application) {

    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    fun getQuestionEditForm(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionEditForm(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    //fun editQuestion(questionId: Long, newTitle: RequestBody, newBody: RequestBody, newTags: List<RequestBody>?, deletedTags: List<RequestBody>?) {
    fun editQuestion(questionId: Long, questionEdit: QuestionEdit){
        CoroutineScope(Dispatchers.IO).launch {
//            val response = questionsRepository?.editQuestion(
//                    sessionManager?.fetchApiToken().toString(),
//                    questionId,
//                    newTitle,
//                    newBody,
//                    newTags,
//                    deletedTags
//            )

            val response = questionsRepository?.editQuestion(
                    sessionManager?.fetchApiToken().toString(),
                    questionId,
                    questionEdit
            )

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun retry(questionId: Long) {
        getQuestionEditForm(questionId)
    }
}