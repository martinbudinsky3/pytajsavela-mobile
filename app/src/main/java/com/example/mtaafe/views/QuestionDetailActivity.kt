package com.example.mtaafe.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionDetailViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class QuestionDetailActivity: AppCompatActivity() {
    private lateinit var viewModel: QuestionDetailViewModel
    private lateinit var rootLayout: View
    private lateinit var answerAdapter: AnswerAdapter

    private var questionId: Long = 0

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_detail)

        questionId = intent.getLongExtra("question_id", 0)

        val answersListRecycler: RecyclerView = findViewById(R.id.answersListRecycler)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionDetailViewModel::class.java)

        answerAdapter = AnswerAdapter(ArrayList())
        answersListRecycler.layoutManager = LinearLayoutManager(this)
        answersListRecycler.adapter = answerAdapter

        viewModel.getQuestionDetails(questionId)

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

        val answerButton : FloatingActionButton = findViewById(R.id.answerBtn)

        answerButton.setOnClickListener {
            val intent = Intent(this, AnswerFormActivity::class.java)
            intent.putExtra("question_id", questionId)
            startActivity(intent)
        }
    }

    private fun setQuestionData(question: Question){
        val questionTitleTV: TextView = findViewById(R.id.questionTitleTV)
        val questionBodyTV: TextView = findViewById(R.id.questionBodyTV)
        val authorTV: TextView = findViewById(R.id.authorTV)
        val createdAtTV: TextView = findViewById(R.id.createdAtTV)
        val answersCountTV: TextView = findViewById(R.id.answersCountTV)

        questionTitleTV.text = question.title
        questionBodyTV.text = question.body
        authorTV.text = question.author.name
        createdAtTV.text = question.createdAt.toString()
        answersCountTV.text = ("Odpovede (" + question.answers.size.toString() + "):")

        val tagsQdetailRecyclerView: RecyclerView = findViewById(R.id.tagsQdetailRecyclerView)
        val adapter = TagAdapter(question.tags)
        val layoutManager = LinearLayoutManager(this)

        answerAdapter.updateData(question.answers)

        tagsQdetailRecyclerView.layoutManager = layoutManager
        tagsQdetailRecyclerView.adapter = adapter
    }

//    @RequiresApi(Build.VERSION_CODES.O)
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