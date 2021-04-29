package com.example.mtaafe.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.repositories.AnswersRepository
import com.example.mtaafe.data.repositories.ImagesRepository
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionDetailViewModel(application: Application): AndroidViewModel(application) {

    private var questionsRepository: QuestionsRepository? = null
    private var answersRepository: AnswersRepository? = null
    private var imagesRepository: ImagesRepository? = null
    var sessionManager: SessionManager? = null

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _questionImages = MutableLiveData<ArrayList<DecodedImage>>()
    val questionImages: LiveData<ArrayList<DecodedImage>>
        get() = _questionImages

    private val _answersImages = MutableLiveData<ArrayList<AnswersDecodedImage>>()
    val answersImages: LiveData<ArrayList<AnswersDecodedImage>>
        get() = _answersImages

    private val _newAnswer = MutableLiveData<Answer>()
    val newAnswer: LiveData<Answer>
        get() = _newAnswer

    private val _successfulQuestionDelete = MutableLiveData<Boolean>()
    val successfulQuestionDelete: LiveData<Boolean>
        get() = _successfulQuestionDelete

    private val _successfulAnswerDelete = MutableLiveData<Boolean>()
    val successfulAnswerDelete: LiveData<Boolean>
        get() = _successfulAnswerDelete

    private val _questionError = MutableLiveData<ErrorEntity>()
    val questionError: LiveData<ErrorEntity>
        get() = _questionError

    private val _questionDeleteError = MutableLiveData<ErrorEntity>()
    val questionDeleteError: LiveData<ErrorEntity>
        get() = _questionDeleteError

    private val _answerDeleteError = MutableLiveData<ErrorEntity>()
    val answerDeleteError: LiveData<ErrorEntity>
        get() = _answerDeleteError

    init {
        questionsRepository = QuestionsRepository()
        answersRepository = AnswersRepository()
        imagesRepository = ImagesRepository()
        sessionManager = SessionManager(application)
        _questionImages.value = ArrayList()
        _answersImages.value = ArrayList()
    }

    fun getQuestionDetails(questionId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = questionsRepository?.getQuestionDetails(sessionManager?.fetchApiToken().toString(), questionId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is Question) {
                            _question.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {
                        _questionError.value = response.error
                    }
                }
            }
        }
    }

    fun getQuestionImage(imageId: Long, index: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = imagesRepository?.getImage(sessionManager?.fetchApiToken().toString(), imageId)

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
            val response = imagesRepository?.getImage(sessionManager?.fetchApiToken().toString(), imageId)

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
                when(response) {
                    is ApiResult.Success -> {
                        _successfulQuestionDelete.value = true
                    }
                    is ApiResult.Error -> {
                        _questionDeleteError.value = response.error
                    }
                }
            }
        }
    }

    fun deleteAnswer(answerId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = answersRepository?.deleteAnswer(sessionManager?.fetchApiToken().toString(), answerId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        _successfulAnswerDelete.value = true
                    }
                    is ApiResult.Error -> {
                        _answerDeleteError.value = response.error
                    }
                }
            }
        }
    }

    fun getAnswer(answerId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = answersRepository?.getAnswer(sessionManager?.fetchApiToken().toString(), answerId)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        if(response.data is Answer) {
                            _newAnswer.value = response.data!!
                        }
                    }
                    is ApiResult.Error -> {

                    }
                }
            }
        }
    }
}