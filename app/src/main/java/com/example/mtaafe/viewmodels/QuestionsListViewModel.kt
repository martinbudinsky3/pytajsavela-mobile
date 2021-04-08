package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.Credentials
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsListViewModel(application: Application): AndroidViewModel(application) {
    private var questionsRepository: QuestionsRepository? = null
    private var sessionManager: SessionManager? = null

    val questions = MutableLiveData<String>()

    init {
        questionsRepository = QuestionsRepository()
        sessionManager = SessionManager(application)
    }

    fun getQuestionsList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionsList(sessionManager?.fetchApiToken().toString())

            withContext(Dispatchers.Main) {
                questions.value = response.toString()
            }
        }
    }
}