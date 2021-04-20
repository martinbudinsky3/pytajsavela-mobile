package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class QuestionsListActivity : DrawerActivity(), IPageButtonClickListener, IQuestionDetailOpener {
    private lateinit var viewModel: QuestionsListViewModel
    private lateinit var questionsListRecycler: RecyclerView
    private lateinit var questionsListRoot: View
    private lateinit var fab: FloatingActionButton
    private lateinit var emptyQuestionsListText: TextView
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_questions_list)

        // inject activity_questions_list layout into drawer layout
        val dynamicContent: LinearLayout = findViewById(R.id.dynamicContent)
        val questionsListView: View = layoutInflater.inflate(R.layout.activity_questions_list, dynamicContent, false)
        dynamicContent.addView(questionsListView)

        supportActionBar?.title = "Otázky"

        questionsListRoot = findViewById(R.id.questionsListRoot)
        fab = findViewById(R.id.fab)
        emptyQuestionsListText = findViewById(R.id.emptyQuestionsListText)
        questionsListRecycler = findViewById(R.id.questionsListRecycler)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionsListViewModel::class.java)

        adapter = QuestionAdapter(ArrayList())
        questionsListRecycler.layoutManager = LinearLayoutManager(this)
        questionsListRecycler.adapter = adapter

        viewModel.getFirstPage()

        viewModel.questionsList.observe(this, {
            if (it.questions.isNotEmpty()) {
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

        fab.setOnClickListener{
            // TODO open activity for question creation
        }
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(
                    questionsListRoot,
                    "Nepodarilo sa načítať otázky",
                    Snackbar.LENGTH_INDEFINITE
                )
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

    override fun openQuestionDetailActivity(questionId: Long) {
        // TODO open question detail activity
    }
}