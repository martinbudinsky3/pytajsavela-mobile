package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.repositories.AnswersRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnswerEditViewModel(application: Application): AndroidViewModel(application) {

    private var answersRepository: AnswersRepository? = null
    private var sessionManager: SessionManager? = null

    private val _getEditDataError= MutableLiveData<ErrorEntity>()
    val getEditDataError: LiveData<ErrorEntity>
        get() = _getEditDataError

    private val _editError= MutableLiveData<ErrorEntity>()
    val editError: LiveData<ErrorEntity>
        get() = _editError

    private val _editData = MutableLiveData<AnswerEdit>()
    val editData: LiveData<AnswerEdit>
        get() = _editData

    private val _successfulEdit = MutableLiveData<Boolean>()
    val successfulEdit: LiveData<Boolean>
        get() = _successfulEdit

    private val _validationError = MutableLiveData<Boolean>()
    val validationError: LiveData<Boolean>
        get() = _validationError

    private val _bodyErrorMessage = MutableLiveData<String>()
    val bodyErrorMessage: LiveData<String>
        get() = _bodyErrorMessage

    init {
        answersRepository = AnswersRepository()
        sessionManager = SessionManager(application)
    }

    fun getAnswerEditForm(answerId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = answersRepository?.getAnswerEditForm(sessionManager?.fetchApiToken().toString(), answerId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is AnswerEdit)
                            _editData.value = response.data!!
                    }
                    is ApiResult.Error -> {
                        _getEditDataError.value = response.error
                    }
                }
            }
        }
    }

    fun editAnswer(answerEdit: AnswerEdit) {
        _validationError.value = false
        if(validate(answerEdit.body)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = answersRepository?.editAnswer(
                    sessionManager?.fetchApiToken().toString(),
                    answerEdit
                )

                withContext(Dispatchers.Main) {
                    when (response) {
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

    private fun validate(body: String?): Boolean {
        var flag = true

        if(body==null || "".equals(body)) {
            _bodyErrorMessage.value = "Pole obsah je povinné"
            flag = false
        }

        return flag
    }
}