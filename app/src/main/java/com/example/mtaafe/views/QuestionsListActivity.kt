package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.google.android.material.snackbar.Snackbar

class QuestionsListActivity : AppCompatActivity(), IPageButtonClickListener {
    private lateinit var viewModel: QuestionsListViewModel
    private lateinit var questionsListRecycler: RecyclerView
    private lateinit var questionsListRoot: View
    private lateinit var emptyQuestionsListText: TextView
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_list)

        supportActionBar?.title = "Otázky"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        questionsListRoot = findViewById(R.id.questionsListRoot)
        emptyQuestionsListText = findViewById(R.id.emptyQuestionsListText)
        questionsListRecycler = findViewById(R.id.questionsListRecycler)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionsListViewModel::class.java)

        adapter = QuestionAdapter(ArrayList<QuestionItem>())
        questionsListRecycler.layoutManager = LinearLayoutManager(this)
        questionsListRecycler.adapter = adapter

        viewModel.getFirstPage()

        viewModel.questionsList.observe(this, {
            if(it.questions.isNotEmpty()) {
                hideEmptyListMessage()
                adapter.updateData(it.questions)
                questionsListRecycler.scrollToPosition(0)
            } else {
                showEmptyListMessage()
            }
        })

        viewModel.error.observe(this, {
            handleError(it)
        })
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(questionsListRoot, "Nepodarilo sa načítať otázky", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.retry()
                    }
                    .show()
            }
        }
    }

    private fun showEmptyListMessage() {
        emptyQuestionsListText.visibility = View.VISIBLE
        questionsListRecycler.visibility = View.GONE
    }

    private fun hideEmptyListMessage() {
        emptyQuestionsListText.visibility = View.GONE
        questionsListRecycler.visibility = View.VISIBLE
    }

    override fun handleFirstPageButtonClick() {
        viewModel.getFirstPage()
    }

    override fun handlePreviousPageButtonClick() {
        viewModel.getPrevioustPage()
    }

    override fun handleNextPageButtonClick() {
        viewModel.getNextPage()
    }

    override fun handleLastPageButtonClick() {
        viewModel.getLastPage()
    }
}