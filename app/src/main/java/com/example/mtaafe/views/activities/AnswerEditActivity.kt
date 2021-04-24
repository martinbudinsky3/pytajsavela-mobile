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
import com.example.mtaafe.viewmodels.AnswerEditViewModel
import com.google.android.material.snackbar.Snackbar

class AnswerEditActivity: AppCompatActivity() {
    private lateinit var viewModel: AnswerEditViewModel
    private lateinit var rootLayout: View
    private lateinit var answerBodyEditText: EditText
    private lateinit var bodyErrorMessageText: TextView

    private var answerId: Long = 0
    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_edit)

        answerId = intent.getLongExtra("answer_id", 0)
        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Editovanie odpovedi"

        rootLayout = findViewById(R.id.answerEditRoot)
        answerBodyEditText = findViewById(R.id.answerBodyEditText)
        bodyErrorMessageText = findViewById(R.id.bodyErrorMessageText)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(AnswerEditViewModel::class.java)

        viewModel.getAnswerEditForm(answerId)

        val answerEditBtn : Button = findViewById(R.id.answerEditBtn)

        answerEditBtn.setOnClickListener {
            editAnswer()
        }

        viewModel.successfulEdit.observe(this, {
            setResult(Constants.ANSWER_UPDATED)
            finish()
        })

        viewModel.editError.observe(this, {
            handleEditError(it)
        })

        viewModel.editData.observe(this, {
            setAnswerData(it)
        })

        viewModel.getEditDataError.observe(this, {
            handleGetEditDataError(it)
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

    private fun editAnswer(){
        val answerEdit = AnswerEdit(
            answerId,
            answerBodyEditText.text.toString()
        )

        viewModel.editAnswer(answerEdit)
    }

    private fun setAnswerData(answer: AnswerEdit){
        answerBodyEditText.setText(answer.body)
    }

    private fun handleGetEditDataError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Odpoveď neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.getAnswerEditForm(answerId)
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
                Snackbar.make(rootLayout, "Odpoveď neexistuje", Snackbar.LENGTH_INDEFINITE)
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
                        editAnswer()
                    }
                    .show()
            }
        }
    }
}