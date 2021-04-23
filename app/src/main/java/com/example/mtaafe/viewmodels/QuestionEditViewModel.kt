package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.*
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

    private val _getEditDataError= MutableLiveData<ErrorEntity>()
    val getEditDataError: LiveData<ErrorEntity>
        get() = _getEditDataError

    private val _editError= MutableLiveData<ErrorEntity>()
    val editError: LiveData<ErrorEntity>
        get() = _editError

    private val _editData = MutableLiveData<Question>()
    val editData: LiveData<Question>
        get() = _editData

    var successfulEdit: MutableLiveData<Boolean> = MutableLiveData()

    val validationError: MutableLiveData<Boolean> = MutableLiveData()
    val titleErrorMessage: MutableLiveData<String> = MutableLiveData()
    val bodyErrorMessage: MutableLiveData<String> = MutableLiveData()

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    fun getQuestionEditForm(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionEditForm(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is Question) {
                            _editData.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _getEditDataError.value = response.error
                    }
                }
            }
        }
    }

    fun editQuestion(questionId: Long, questionEdit: QuestionEdit) {
        validationError.value = false
        if(validate(questionEdit.title, questionEdit.body)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = questionsRepository?.editQuestion(
                    sessionManager?.fetchApiToken().toString(),
                    questionId,
                    questionEdit
                )

                withContext(Dispatchers.Main) {
                    when(response) {
                        is ApiResult.Success -> {
                            successfulEdit.value = true
                        }
                        is ApiResult.Error -> {
                            _editError.value = response.error
                        }
                    }
                }
            }
        } else {
            validationError.value = true
        }
    }

    private fun validate(title: String?, body: String?): Boolean {
        var flag = true
        if(title==null || "".equals(title)) {
            titleErrorMessage.value = "Pole nadpis je povinné"
            flag = false
        }
        else if(title.length > 255) {
            titleErrorMessage.value = "Pole nadpis musí mať max. 255 znakov"
            flag = false
        }

        if(body==null || "".equals(body)) {
            bodyErrorMessage.value = "Pole obsah je povinné"
            flag = false
        }

        return flag
    }

    fun retry(questionId: Long) {
        getQuestionEditForm(questionId)
    }
}