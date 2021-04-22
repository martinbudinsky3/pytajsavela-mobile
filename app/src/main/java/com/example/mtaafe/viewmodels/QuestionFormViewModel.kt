package com.example.mtaafe.viewmodels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mtaafe.data.models.*
import com.example.mtaafe.data.repositories.QuestionsRepository
import com.example.mtaafe.data.repositories.TagsRepository
import com.example.mtaafe.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.ArrayList

class QuestionFormViewModel(application: Application): AndroidViewModel(application) {
    private var questionsRepository: QuestionsRepository? = null
    private var tagsRepository: TagsRepository? = null
    private var sessionManager: SessionManager? = null

    var result: MutableLiveData<ApiResult<out Any>> = MutableLiveData()

    private val _tagsList = MutableLiveData<TagsList>()
    val tagsList: LiveData<TagsList>
        get() = _tagsList

    private val _errorTagsList = MutableLiveData<ErrorEntity>()
    val errorTagsList: LiveData<ErrorEntity>
        get() = _errorTagsList

    val validationError: MutableLiveData<Boolean> = MutableLiveData()
    val titleErrorMessage: MutableLiveData<String> = MutableLiveData()
    val bodyErrorMessage: MutableLiveData<String> = MutableLiveData()

    var searchQuery: String = ""

    init {
        questionsRepository = QuestionsRepository()
        tagsRepository = TagsRepository()
        sessionManager = SessionManager(application)
    }

     fun postQuestion(title : String, body : String, tags : List<Long>, images : List<MultipartBody.Part>?){
         validationError.value = false
         if(validate(title, body)) {
             CoroutineScope(Dispatchers.IO).launch {
                 val response = questionsRepository!!.postQuestion(
                     sessionManager?.fetchApiToken().toString(),
                     createPartFromString(title),
                     createPartFromString(body),
                     tags,
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

    fun getTagsList(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = tagsRepository?.getTagsList(sessionManager?.fetchApiToken().toString(), 1, searchQuery)

            withContext(Dispatchers.Main) {
                when(response) {
                    is ApiResult.Success -> {

                        if(response.data is TagsList) {
                            _tagsList.value = response.data!!

                        }
                    }
                    is ApiResult.Error -> {
                        _errorTagsList.value = response.error
                    }
                }
            }
        }
    }

    private fun validate(title: String?, body: String?): Boolean {
        var flag = true
        if(title==null || "".equals(title)) {
            titleErrorMessage.value = "Pole nadpis je povinné"
            flag = false
        }
        else if(title.length > 255) {
            titleErrorMessage.value = "Pole nadpis musí mať max. 255 znakov"
            flag = false
        }

        if(body==null || "".equals(body)) {
            bodyErrorMessage.value = "Pole obsah je povinné"
            flag = false
        }

        return flag
    }

    private fun createPartFromString(partString: String) : RequestBody{
        return RequestBody.create(MultipartBody.FORM, partString)
    }
}