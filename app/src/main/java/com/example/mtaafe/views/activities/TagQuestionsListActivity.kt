package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.TagQuestionsListViewModel
import com.example.mtaafe.views.adapters.QuestionAdapter
import com.google.android.material.snackbar.Snackbar

class TagQuestionsListActivity : AppCompatActivity(), IPageButtonClickListener,
    IQuestionDetailOpener {
    private lateinit var viewModel: TagQuestionsListViewModel
    private lateinit var tagQuestionsListRecycler: RecyclerView
    private lateinit var questionsListRoot: View
    private lateinit var emptyTagQuestionsListText: TextView
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_questions_list)

        val tagId = intent.getLongExtra("tagId", 1)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        questionsListRoot = findViewById(R.id.questionsListRoot)
        emptyTagQuestionsListText = findViewById(R.id.emptyTagQuestionsListText)

        tagQuestionsListRecycler = findViewById(R.id.tagQuestionsListRecycler)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(TagQuestionsListViewModel::class.java)
        viewModel.tagId = tagId

        adapter = QuestionAdapter(ArrayList())
        tagQuestionsListRecycler.layoutManager = LinearLayoutManager(this)
        tagQuestionsListRecycler.adapter = adapter

        viewModel.getFirstPage()

        viewModel.tagQuestionsList.observe(this, {
            supportActionBar?.title = it.tag.name
            if(it.questions.isNotEmpty()) {
                hideEmptyListMessage()
                adapter.updateData(it.questions)
                tagQuestionsListRecycler.scrollToPosition(0)
            } else {
                showEmptyListMessage()
            }
        })

        viewModel.error.observe(this, {
            handleError(it)
        })
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(questionsListRoot, "Tag neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                Log.d("TagQuestionsListActivit", "Back button pressed")
                onBackPressed()
                return true
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun showEmptyListMessage() {
        emptyTagQuestionsListText.visibility = View.VISIBLE
        tagQuestionsListRecycler.visibility = View.GONE
    }

    private fun hideEmptyListMessage() {
        emptyTagQuestionsListText.visibility = View.GONE
        tagQuestionsListRecycler.visibility = View.VISIBLE
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
        val intent = Intent(this, QuestionDetailActivity::class.java)
        intent.putExtra("question_id", questionId)
        startActivity(intent)
    }
}