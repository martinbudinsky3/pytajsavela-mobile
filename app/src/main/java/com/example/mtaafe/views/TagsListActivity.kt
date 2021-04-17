package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants.TAGS_LIST_COLS
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.TagsListViewModel
import com.google.android.material.snackbar.Snackbar

class TagsListActivity : AppCompatActivity(), IPageButtonClickListener {
    //lateinit var binding: ActivityTagsListBinding
    private lateinit var viewModel: TagsListViewModel
    private lateinit var rootLayout: View
    private lateinit var adapter: TagDetailAdapter
    private lateinit var searchText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags_list)

        rootLayout = findViewById(R.id.tagsListRoot)
        //rootLayout = findViewById(R.id.content)
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
        val tagsListRecycler: RecyclerView = findViewById(R.id.tagsListRecycler)
        tagsListRecycler.layoutManager = layoutManager
        tagsListRecycler.adapter = adapter

        searchText = findViewById(R.id.searchText)
        searchText.setText(viewModel.searchQuery)
        initSearch()

        viewModel.getFirstPage()

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is TagsList) {
                        adapter.updateData(it.data.tags)
                        tagsListRecycler.scrollToPosition(0)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })
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

        /*// handle search queries
        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                viewModel.searchQuery = searchText.text.toString()
                viewModel.getFirstPage()
                true
            } else {
                false
            }
        }

        searchText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.searchQuery = searchText.text.toString()
                viewModel.getFirstPage()
                true
            } else {
                false
            }
        }*/
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
