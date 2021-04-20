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
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.AnswerEditViewModel
import com.example.mtaafe.viewmodels.QuestionEditViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AnswerEditActivity: AppCompatActivity() {
    private lateinit var viewModel: AnswerEditViewModel
    private lateinit var rootLayout: View

    private lateinit var answerBodyEditET: EditText

    private var answerId: Long = 0
    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_edit)

        answerId = intent.getLongExtra("answer_id", 0)
        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        answerBodyEditET = findViewById(R.id.answerBodyEditET)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(AnswerEditViewModel::class.java)

        viewModel.getAnswerEditForm(answerId)

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
        //val newBody = createPartFromString(answerBodyEditET.text.toString())

        Log.d("msg", "NEW BODY: " + answerBodyEditET.text.toString())

        val answerEdit = AnswerEdit(
                answerId,
                answerBodyEditET.text.toString()
        )

        viewModel.editAnswer(answerEdit)

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    Log.d("Success", "Answer was edited.")

                    val intent = Intent(this, QuestionDetailActivity::class.java)
                    intent.putExtra("question_id", questionId)
                    startActivity(intent)
                    finish()
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })
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
                            viewModel.retry(answerId)
                        }
                        .show()
            }
        }
    }
}

data class AnswerEdit (
        @SerializedName("id")
        var id: Long,

        @SerializedName("body")
        var body: String,
)