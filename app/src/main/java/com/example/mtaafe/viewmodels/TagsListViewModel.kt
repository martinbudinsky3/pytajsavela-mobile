package com.example.mtaafe.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.QuestionsList
import com.example.mtaafe.data.models.TagsList
import com.example.mtaafe.data.repositories.TagsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagsListViewModel(application: Application): AndroidViewModel(application) {
    private val PAGE_SIZE: Int = 10

    private var tagsRepository: TagsRepository? = null
    private var sessionManager: SessionManager? = null
    private var currentPage: Int = 1;
    private var count: Long = 0

    private val _tagsList = MutableLiveData<TagsList>()
    val tagsList: LiveData<TagsList>
        get() = _tagsList

    private val _error = MutableLiveData<ErrorEntity>()
    val error: LiveData<ErrorEntity>
        get() = _error

    var searchQuery: String = ""

    init {
        tagsRepository = TagsRepository()
        sessionManager = SessionManager(application)
    }

    private fun getTagsList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = tagsRepository?.getTagsList(sessionManager?.fetchApiToken().toString(), page, searchQuery)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {
                        currentPage = page

                        if(response.data is TagsList) {
                            count = response.data.count
                            _tagsList.value = response.data!!

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
        getTagsList(1)
    }

    fun getPrevioustPage() {
        getTagsList(currentPage - 1)
    }

    fun getNextPage() {
        Log.d("Questions list api call", (currentPage + 1).toString())
        getTagsList(currentPage + 1)
    }

    fun getLastPage() {
        var lastPage = count / PAGE_SIZE
        if(count % PAGE_SIZE != 0L) {
            lastPage++
        }
        getTagsList(lastPage.toInt())
    }

    fun retry() {
        getTagsList(currentPage)
    }
}