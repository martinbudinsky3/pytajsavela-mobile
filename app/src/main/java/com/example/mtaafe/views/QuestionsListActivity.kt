package com.example.mtaafe.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class QuestionsListActivity : AppCompatActivity(), IPageButtonClickListener, OnQuestionClickListener {
    private lateinit var viewModel: QuestionsListViewModel
    private lateinit var rootLayout: View
    private lateinit var adapter: QuestionAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_list)

        rootLayout = findViewById(R.id.questionsListRoot)

        val questionsListRecycler: RecyclerView = findViewById(R.id.questionsListRecycler)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionsListViewModel::class.java)

        adapter = QuestionAdapter(ArrayList<QuestionItem>(), this)
        questionsListRecycler.layoutManager = LinearLayoutManager(this)
        questionsListRecycler.adapter = adapter

        viewModel.getFirstPage()

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is QuestionsList) {
                        //questionsListRecycler.adapter = QuestionAdapter(it.data.questions)
                        adapter.updateData(it.data.questions)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })

        val askQuestionBtn: FloatingActionButton = findViewById(R.id.askQuestionBtn)

        askQuestionBtn.setOnClickListener(){
            val intent = Intent(this, QuestionFormActivity::class.java)
            startActivity(intent)
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
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.retry()
                    }
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleFirstPageButtonClick() {
        viewModel.getFirstPage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handlePreviousPageButtonClick() {
        viewModel.getPrevioustPage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleNextPageButtonClick() {
        viewModel.getNextPage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleLastPageButtonClick() {
        viewModel.getLastPage()
    }

    override fun onQuestionClick(position: Int) {
        val clickedQuestion: QuestionItem = adapter.getQuestion(position)
        Log.d("qes", "Question with id = " + clickedQuestion.id + " was clicked!")

        val intent = Intent(this, QuestionDetailActivity::class.java)
        intent.putExtra("question_id", clickedQuestion.id)
        startActivity(intent)
    }
}