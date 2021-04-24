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

    private val _successfulEdit = MutableLiveData<Boolean>()
    val successfulEdit: LiveData<Boolean>
        get() = _successfulEdit

    private val _validationError = MutableLiveData<Boolean>()
    val validationError: LiveData<Boolean>
        get() = _validationError

    private val _titleErrorMessage = MutableLiveData<String>()
    val titleErrorMessage: LiveData<String>
        get() = _titleErrorMessage

    private val _bodyErrorMessage = MutableLiveData<String>()
    val bodyErrorMessage: LiveData<String>
        get() = _bodyErrorMessage

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
        _validationError.value = false
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
                            _successfulEdit.value = true
                        }
                        is ApiResult.Error -> {
                            _editError.value = response.error
                        }
                    }
                }
            }
        } else {
            _validationError.value = true
        }
    }

    private fun validate(title: String?, body: String?): Boolean {
        var flag = true
        if(title==null || "".equals(title)) {
            _titleErrorMessage.value = "Pole nadpis je povinné"
            flag = false
        }
        else if(title.length > 255) {
            _titleErrorMessage.value = "Pole nadpis musí mať max. 255 znakov"
            flag = false
        }

        if(body==null || "".equals(body)) {
            _bodyErrorMessage.value = "Pole obsah je povinné"
            flag = false
        }

        return flag
    }
}