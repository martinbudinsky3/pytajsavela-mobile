package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Question
import com.example.mtaafe.data.models.Tag
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.ArrayList

//@RequiresApi(Build.VERSION_CODES.O)
class QuestionFormViewModel(application: Application): AndroidViewModel(application) {
    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null

    var result: MutableLiveData<ApiResult<out Any>> = MutableLiveData()

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

     fun postQuestion(title : RequestBody, body : RequestBody, tags : List<Long>, images : List<MultipartBody.Part>?){
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository!!.postQuestion(
                    sessionManager?.fetchApiToken().toString(),
                    title,
                    body,
                    tags,
                    images
            )

            withContext(Dispatchers.Main) {
                result.value = response
            }
        }

    }
}