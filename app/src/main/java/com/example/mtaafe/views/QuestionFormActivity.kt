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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.databinding.ActivityLoginBinding
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import androidx.lifecycle.Observer
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionFormViewModel
import com.google.android.material.snackbar.Snackbar

class QuestionFormActivity : AppCompatActivity() {
    private lateinit var viewModel: QuestionFormViewModel
    private lateinit var rootLayout: View
    private lateinit var adapter: QuestionAdapter

    val editTextQuestionTitle: EditText = findViewById(R.id.editTextQuestionTitle)
    val editTextQuestionBody: EditText = findViewById(R.id.editTextQuestionBody)
    val editTextQuestionTags: EditText = findViewById(R.id.editTextQuestionTags)

    val askButton : Button = findViewById(R.id.askButton)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_form)
        rootLayout = findViewById(R.id.questionFormRoot)

        val title = editTextQuestionTitle.text.toString()
        val body = editTextQuestionBody.text.toString()

        val question = Question(1, title, body, listOf(Tag(1, "nejaky-tag")), listOf())

        askButton.setOnClickListener {
            viewModel.postQuestion(question)

            viewModel.result.observe(this, Observer {
                when(it) {
                    is ApiResult.Success -> {
                        Log.d("Success", "Question was posted.")
                    }
                    is ApiResult.Error -> handleError(it.error)
                    else -> {}
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, something went wrong.", Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }
}