package com.example.mtaafe.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Answer
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.Question
import com.example.mtaafe.viewmodels.AnswerEditViewModel
import com.example.mtaafe.viewmodels.QuestionEditViewModel
import com.google.android.material.snackbar.Snackbar

class AnswerEditActivity: AppCompatActivity() {
    private lateinit var viewModel: AnswerEditViewModel
    private lateinit var rootLayout: View

    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_edit)

        questionId = intent.getLongExtra("question_id", 0)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(AnswerEditViewModel::class.java)

        viewModel.getQuestionDetails(questionId)

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is Answer) {
                        setAnswerData(it.data)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })

        val answerEditBtn : Button = findViewById(R.id.answerEditBtn)

        answerEditBtn.setOnClickListener {
            editAnswer()
        }
    }

    private fun editAnswer(){

    }

    private fun setAnswerData(answer: Answer){
        val answerBodyEditET: EditText = findViewById(R.id.answerBodyEditET)
        answerBodyEditET.setText(answer.body)
    }

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