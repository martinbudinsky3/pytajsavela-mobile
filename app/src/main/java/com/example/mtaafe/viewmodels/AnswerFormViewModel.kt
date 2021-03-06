package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Question
import com.example.mtaafe.data.repositories.AnswersRepository
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AnswerFormViewModel(application: Application): AndroidViewModel(application) {
    private var answersRepository: AnswersRepository? = null
    private var sessionManager: SessionManager? = null

    var result: MutableLiveData<ApiResult<out Any>> = MutableLiveData()
    val validationError: MutableLiveData<Boolean> = MutableLiveData()
    val bodyErrorMessage: MutableLiveData<String> = MutableLiveData()

    init {
        sessionManager = SessionManager(application)
        answersRepository = AnswersRepository()
    }

    fun postAnswer(questionId: Long, body : String, images : List<MultipartBody.Part>?) {
        validationError.value = false
        if(validate(body)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = answersRepository?.postAnswer(
                    sessionManager?.fetchApiToken().toString(),
                    questionId,
                    createPartFromString(body),
                    images
                )

                withContext(Dispatchers.Main) {
                    result.value = response
                }
            }
        } else {
            validationError.value = true
        }
    }

    private fun validate(body: String?): Boolean {
        var flag = true

        if(body==null || "".equals(body)) {
            bodyErrorMessage.value = "Pole obsah je povinn??"
            flag = false
        }

        return flag
    }

    private fun createPartFromString(partString: String) : RequestBody{
        return RequestBody.create(MultipartBody.FORM, partString)
    }
}