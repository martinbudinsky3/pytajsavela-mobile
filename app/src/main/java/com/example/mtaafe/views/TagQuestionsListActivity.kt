package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.TagQuestionsListViewModel
import com.google.android.material.snackbar.Snackbar

class TagQuestionsListActivity : AppCompatActivity(), IPageButtonClickListener {
    private lateinit var viewModel: TagQuestionsListViewModel
    private lateinit var rootLayout: View
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tag_questions_list)
        val tagId = intent.getLongExtra("tagId", 1);
        rootLayout = findViewById(R.id.questionsListRoot)

        val tagQuestionsListRecycler: RecyclerView = findViewById(R.id.tagQuestionsListRecycler)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(TagQuestionsListViewModel::class.java)
        viewModel.tagId = tagId

        adapter = QuestionAdapter(ArrayList())
        tagQuestionsListRecycler.layoutManager = LinearLayoutManager(this)
        tagQuestionsListRecycler.adapter = adapter

        viewModel.getFirstPage()

        viewModel.tagQuestionsList.observe(this, {
            adapter.updateData(it.questions)
            tagQuestionsListRecycler.scrollToPosition(0)
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
                Snackbar.make(rootLayout, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.retry()
                    }
                    .show()
            }
        }
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