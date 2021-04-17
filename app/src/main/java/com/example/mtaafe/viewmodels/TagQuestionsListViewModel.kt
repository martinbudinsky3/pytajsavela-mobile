package com.example.mtaafe.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
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

    private val _result = MutableLiveData<ApiResult<out Any>>()
    val result: LiveData<ApiResult<out Any>>
        get() = _result

    var tagId: Long = 1

    init {
        tagsRepository = TagsRepository()
        sessionManager = SessionManager(application)
    }

    private fun getTagQuestionsList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tagsRepository?.getTagQuestionsList(sessionManager?.fetchApiToken().toString(), tagId, page)

            withContext(Dispatchers.Main) {
                _result.value = response!!

                if(response is ApiResult.Success) {
                    currentPage = page

                    if(response.data is TagQuestionsList) {
                        count = response.data.count
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