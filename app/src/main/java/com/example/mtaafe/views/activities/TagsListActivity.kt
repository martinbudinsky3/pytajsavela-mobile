package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants.TAGS_LIST_COLS
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.TagsListViewModel
import com.example.mtaafe.views.adapters.TagDetailAdapter
import com.google.android.material.snackbar.Snackbar

class TagsListActivity : DrawerActivity(), IPageButtonClickListener {
    private lateinit var viewModel: TagsListViewModel
    private lateinit var tagsListRecycler: RecyclerView
    private lateinit var rootLayout: View
    private lateinit var emptyTagsListText: TextView
    private lateinit var adapter: TagDetailAdapter
    private lateinit var searchText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_tags_list)

        // inject activity_tags_list layout into drawer layout
        val dynamicContent: LinearLayout = findViewById(R.id.dynamicContent)
        val questionsListView: View = layoutInflater.inflate(R.layout.activity_tags_list, dynamicContent, false)
        dynamicContent.addView(questionsListView)

        supportActionBar?.title = "Tagy"

        rootLayout = findViewById(R.id.tagsListRoot)
        emptyTagsListText = findViewById(R.id.emptyTagsListText)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(TagsListViewModel::class.java)

        adapter = TagDetailAdapter(ArrayList<Tag>())
        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == adapter.TYPE_FOOTER) TAGS_LIST_COLS else 1
            }
        }
        val layoutManager = GridLayoutManager(this, TAGS_LIST_COLS)
        layoutManager.spanSizeLookup = spanSizeLookup
        tagsListRecycler = findViewById(R.id.tagsListRecycler)
        tagsListRecycler.layoutManager = layoutManager
        tagsListRecycler.adapter = adapter

        searchText = findViewById(R.id.searchText)
        searchText.setText(viewModel.searchQuery)
        initSearch()

        viewModel.getFirstPage()

        viewModel.tagsList.observe(this, {
            if(it.tags.isNotEmpty()) {
                hideEmptyListMessage()
                adapter.updateData(it.tags)
                tagsListRecycler.scrollToPosition(0)
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
                Snackbar.make(rootLayout, "Nepodarilo sa načítať tagy", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.retry()
                    }
                    .show()
            }
        }
    }

    private fun initSearch() {
        searchText.doOnTextChanged { text, _, _, _ ->
            viewModel.searchQuery = text.toString()
            viewModel.getFirstPage()
        }
    }

    private fun showEmptyListMessage() {
        emptyTagsListText.visibility = View.VISIBLE
        tagsListRecycler.visibility = View.GONE
    }

    private fun hideEmptyListMessage() {
        emptyTagsListText.visibility = View.GONE
        tagsListRecycler.visibility = View.VISIBLE
    }

    fun openTagQuestionsListActivity(tagId: Long) {
        val intent = Intent(this, TagQuestionsListActivity::class.java)
        intent.putExtra("tagId", tagId)
        startActivity(intent)
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
