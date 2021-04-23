package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
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

    private val _editData = MutableLiveData<Answer>()
    val editData: LiveData<Answer>
        get() = _editData

    var successfulEdit: MutableLiveData<Boolean> = MutableLiveData()
    val validationError: MutableLiveData<Boolean> = MutableLiveData()
    val bodyErrorMessage: MutableLiveData<String> = MutableLiveData()

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
                        if(response.data is Answer)
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
        validationError.value = false
        if(validate(answerEdit.body)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = answersRepository?.editAnswer(
                    sessionManager?.fetchApiToken().toString(),
                    answerEdit
                )

                withContext(Dispatchers.Main) {
                    when (response) {
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

    private fun validate(body: String?): Boolean {
        var flag = true

        if(body==null || "".equals(body)) {
            bodyErrorMessage.value = "Pole obsah je povinné"
            flag = false
        }

        return flag
    }
}