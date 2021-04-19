package com.example.mtaafe.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionEditViewModel
import com.google.android.material.snackbar.Snackbar
import okhttp3.MultipartBody
import okhttp3.RequestBody

class QuestionEditActivity: AppCompatActivity() {
    private lateinit var viewModel: QuestionEditViewModel
    private lateinit var rootLayout: View

    private var questionTitleEditET: EditText? = null
    private var questionBodyEditET: EditText? = null
    private var tagsEditET: EditText? = null

    private lateinit var originalTags: List<Tag>

    private var questionId: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_edit)

        questionId = intent.getLongExtra("question_id", 0)

        questionTitleEditET = findViewById(R.id.questionTitleEditET)
        questionBodyEditET = findViewById(R.id.questionBodyEditET)
        tagsEditET = findViewById(R.id.tagsEditET)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionEditViewModel::class.java)

        viewModel.getQuestionEditForm(questionId)

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is Question) {
                        setQuestionData(it.data)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })

        val questionEditBtn : Button = findViewById(R.id.questionEditBtn)

        questionEditBtn.setOnClickListener {
            editQuestion()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun editQuestion(){
        val newTitle = createPartFromString(questionTitleEditET?.text.toString())
        val newBody = createPartFromString(questionBodyEditET?.text.toString())

        Log.d("msg", "NEW TITLE: " + questionTitleEditET?.text.toString())
        Log.d("msg", "NEW BODY: " + questionBodyEditET?.text.toString())

        val newTagsString = tagsEditET?.text.toString().split(",").map { it.trim() }
        val deletedTagsString = getDeletedTags(originalTags, newTagsString)

        val newTagsPart : List<RequestBody>? = getTags(newTagsString)
        val deletedTagsPart : List<RequestBody>? = getTags(deletedTagsString!!)

        val questionEdit = QuestionEdit(questionId,
                questionTitleEditET?.text.toString(),
                questionBodyEditET?.text.toString(),
                originalTags,
                originalTags
        )
        //viewModel.editQuestion(questionId, newTitle, newBody, newTagsPart, deletedTagsPart)
        viewModel.editQuestion(questionId, questionEdit)
    }

    private fun createPartFromString(partString : String) : RequestBody {
        return RequestBody.create(MultipartBody.FORM, partString)
    }

    private fun getTags(tagsString : List<String>): List<RequestBody>? {
        val tagsRequestBody = mutableListOf<RequestBody>()

        tagsString.forEachIndexed{index, element -> tagsRequestBody.add(index, createPartFromString(element)) }

        return tagsRequestBody
    }

    private fun getDeletedTags(originalTags: List<Tag>, newTags: List<String>): List<String>?{
        val deletedTags = mutableListOf<String>()

        for (tag in originalTags){
            if (tag.name !in newTags){
                deletedTags.add(tag.name)
            }
        }

        return deletedTags
    }

    private fun setQuestionData(question: Question){
        questionTitleEditET?.setText(question.title)
        questionBodyEditET?.setText(question.body)

        originalTags = question.tags

        val tagsEditRecyclerView: RecyclerView = findViewById(R.id.tagsEditRecyclerView)
        val adapter = TagAdapter(question.tags)
        val layoutManager = LinearLayoutManager(this)

        tagsEditRecyclerView.layoutManager = layoutManager
        tagsEditRecyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_LONG)
                        .setAction("Skúsiť znovu") {
                            viewModel.retry(questionId)
                        }
                        .show()
            }
        }
    }
}