package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.Question
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
class QuestionFormViewModel(application: Application): AndroidViewModel(application) {
//    private var questionsRepository: QuestionsRepository? = null
//    var result: MutableLiveData<ApiResult<out Any>> = MutableLiveData()
//
//    init {
//        questionsRepository = QuestionsRepository()
//    }
//
//     fun postQuestion(question : Question){
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = questionsRepository?.postQuestion(question)
//
//            result.value = response
//        }
//
//    }
}