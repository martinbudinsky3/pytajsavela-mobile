package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionEditViewModel
import com.google.android.material.snackbar.Snackbar

class QuestionEditActivity: AppCompatActivity() {
    private lateinit var viewModel: QuestionEditViewModel
    private lateinit var rootLayout: View
    private lateinit var questionTitleEditText: EditText
    private lateinit var questionBodyEditText: EditText
    private lateinit var titleErrorMessageText: TextView
    private lateinit var bodyErrorMessageText: TextView

    private lateinit var originalTags: List<Tag>
    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_edit)

        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Editovanie otázky"

        rootLayout = findViewById(R.id.questionEditRoot)
        questionTitleEditText = findViewById(R.id.questionTitleEditText)
        questionBodyEditText = findViewById(R.id.questionBodyEditText)
        titleErrorMessageText = findViewById(R.id.titleErrorMessageText)
        bodyErrorMessageText = findViewById(R.id.bodyErrorMessageText)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionEditViewModel::class.java)

        viewModel.getQuestionEditForm(questionId)

        val questionEditBtn : Button = findViewById(R.id.questionEditBtn)

        questionEditBtn.setOnClickListener {
            editQuestion()
        }


        viewModel.successfulEdit.observe(this, {
            if(it == true) {
                setResult(Constants.QUESTION_UPDATED)
                finish()
            }
        })

        viewModel.editError.observe(this, {
            handleEditError(it)
        })

        viewModel.editData.observe(this, {
            setQuestionData(it)
        })

        viewModel.getEditDataError.observe(this, {
            handleGetEditDataError(it)
        })

        viewModel.titleErrorMessage.observe(this, {
            titleErrorMessageText.visibility = View.VISIBLE
            titleErrorMessageText.text = it
        })

        viewModel.bodyErrorMessage.observe(this, {
            bodyErrorMessageText.visibility = View.VISIBLE
            bodyErrorMessageText.text = it
        })

        viewModel.validationError.observe(this, {
            if(it == true) {
                Snackbar.make(rootLayout, "Nevyplnili ste správne všetky polia", Snackbar.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun editQuestion(){
        val tgs = ArrayList<Long>()
        val questionEdit = QuestionEdit(
            questionId,
            questionTitleEditText?.text.toString(),
            questionBodyEditText?.text.toString(),
            tgs,
            tgs
        )

        bodyErrorMessageText.visibility = View.GONE
        titleErrorMessageText.visibility = View.GONE

        viewModel.editQuestion(questionId, questionEdit)
    }

//    private fun getDeletedTags(originalTags: List<Tag>, newTags: List<String>): List<String>?{
//        val deletedTags = mutableListOf<String>()
//
//        for (tag in originalTags){
//            if (tag.name !in newTags){
//                deletedTags.add(tag.name)
//            }
//        }
//
//        return deletedTags
//    }

    private fun setQuestionData(question: Question){
        questionTitleEditText?.setText(question.title)
        questionBodyEditText?.setText(question.body)

//        originalTags = question.tags
//
//        val tagsEditRecyclerView: RecyclerView = findViewById(R.id.tagsEditRecyclerView)
//        val adapter = TagAdapter(question.tags)
//        val layoutManager = LinearLayoutManager(this)
//
//        tagsEditRecyclerView.layoutManager = layoutManager
//        tagsEditRecyclerView.adapter = adapter
    }

    private fun handleGetEditDataError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Otázka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.getQuestionEditForm(questionId)
                    }
                    .show()
            }
        }
    }

    private fun handleEditError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Otázka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            is ErrorEntity.AccessDenied -> {
                Snackbar.make(rootLayout, "Na danú akciu nemate práva", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        editQuestion()
                    }
                    .show()
            }
        }
    }
}