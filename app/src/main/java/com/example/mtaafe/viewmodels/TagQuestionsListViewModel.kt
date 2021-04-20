package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.QuestionsList
import com.example.mtaafe.data.models.TagQuestionsList
import com.example.mtaafe.data.repositories.TagsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagQuestionsListViewModel(application: Application): AndroidViewModel(application) {
    private val PAGE_SIZE: Int = 10

    private var tagsRepository: TagsRepository? = null
    private var sessionManager: SessionManager? = null
    private var currentPage: Int = 1;
    private var count: Int = 0

    private val _tagQuestionsList = MutableLiveData<TagQuestionsList>()
    val tagQuestionsList: LiveData<TagQuestionsList>
        get() = _tagQuestionsList

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    var tagId: Long = 1

    init {
        tagsRepository = TagsRepository()
        sessionManager = SessionManager(application)
    }

    private fun getTagQuestionsList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tagsRepository?.getTagQuestionsList(sessionManager?.fetchApiToken().toString(), tagId, page)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        currentPage = page

                        if(response.data is TagQuestionsList) {
                            count = response.data.count
                            _tagQuestionsList.value = response.data!!

                        }
                    }
                    is ApiResult.Error -> {
                        _error.value = response.error
                    }
                }
            }
        }
    }

    fun getFirstPage() {
        getTagQuestionsList(1)
    }

    fun getPrevioustPage() {
        getTagQuestionsList(currentPage - 1)
    }

    fun getNextPage() {
        getTagQuestionsList(currentPage + 1)
    }

    fun getLastPage() {
        var lastPage = count / PAGE_SIZE
        if(count % PAGE_SIZE != 0) {
            lastPage++
        }
        getTagQuestionsList(lastPage)
    }

    fun retry() {
        getTagQuestionsList(currentPage)
    }
}