package com.example.mtaafe.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.AnswersDecodedImage
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.DecodedImage
import com.example.mtaafe.data.repositories.AnswersRepository
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionDetailViewModel(application: Application): AndroidViewModel(application) {

    private var questionsRepository: QuestionsRepository? = null
    private var answersRepository: AnswersRepository? = null
    var sessionManager: SessionManager? = null

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    private val _questionImages = MutableLiveData<ArrayList<DecodedImage>>()
    val questionImages: LiveData<ArrayList<DecodedImage>>
        get() = _questionImages

    private val _answersImages = MutableLiveData<ArrayList<AnswersDecodedImage>>()
    val answersImages: LiveData<ArrayList<AnswersDecodedImage>>
        get() = _answersImages

    init {
        questionsRepository = QuestionsRepository()
        answersRepository = AnswersRepository()
        sessionManager = SessionManager(application)
        _questionImages.value = ArrayList()
        _answersImages.value = ArrayList()
    }

    fun getQuestionDetails(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionDetails(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun getQuestionImage(imageId: Long, index: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getImage(sessionManager?.fetchApiToken().toString(), imageId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is Bitmap) {
                            _questionImages.value?.add(DecodedImage(index, response.data))
                            _questionImages.value = _questionImages.value
                        }
                    }
                    is ApiResult.Error -> {

                    }
                }
            }
        }
    }

    fun getAnswerImage(imageId: Long, imageIndex: Int, answerIndex: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getImage(sessionManager?.fetchApiToken().toString(), imageId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is Bitmap) {
                            _answersImages.value?.add(AnswersDecodedImage(answerIndex, imageIndex, response.data))
                            _answersImages.value = _answersImages.value
                        }
                    }
                    is ApiResult.Error -> {

                    }
                }
            }
        }
    }

    fun deleteQuestion(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.deleteQuestion(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun deleteAnswer(answerId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = answersRepository?.deleteAnswer(sessionManager?.fetchApiToken().toString(), answerId)

            withContext(Dispatchers.Main) {
                _result.value = response!!
            }
        }
    }

    fun retry(questionId: Long) {
        getQuestionDetails(questionId)
    }
}