package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionEditViewModel
import com.example.mtaafe.views.adapters.DeletableTagAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar

class QuestionEditActivity: AppCompatActivity(), TagDeleteClickListener {
    private lateinit var viewModel: QuestionEditViewModel
    private lateinit var selectedTagsAdapter: DeletableTagAdapter
    private lateinit var tagsAdapter: ArrayAdapter<Tag>
    private lateinit var rootLayout: View
    private lateinit var tagsRecycler: RecyclerView
    private lateinit var questionTitleEditText: EditText
    private lateinit var questionBodyEditText: EditText
    private lateinit var titleErrorMessageText: TextView
    private lateinit var bodyErrorMessageText: TextView
    private lateinit var questionEditBtn : Button
    private lateinit var tagsAutoCompleteTextView: AutoCompleteTextView

    private var newTags = ArrayList<Long>()
    private var deletedTags = ArrayList<Long>()
    private var originalTagsSize = 0
    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_edit)

        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Editovanie otázky"

        rootLayout = findViewById(R.id.questionEditRoot)
        tagsRecycler = findViewById(R.id.tagsRecyclerView)
        questionTitleEditText = findViewById(R.id.questionTitleEditText)
        questionBodyEditText = findViewById(R.id.questionBodyEditText)
        titleErrorMessageText = findViewById(R.id.titleErrorMessageText)
        bodyErrorMessageText = findViewById(R.id.bodyErrorMessageText)
        questionEditBtn = findViewById(R.id.questionEditBtn)
        tagsAutoCompleteTextView = findViewById(R.id.tagsAutoCompleteTextView)

        questionEditBtn.setOnClickListener {
            editQuestion()
        }

        selectedTagsAdapter = DeletableTagAdapter(ArrayList())
        tagsRecycler.layoutManager = FlexboxLayoutManager(this)
        tagsRecycler.adapter = selectedTagsAdapter

        tagsAdapter = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, ArrayList<Tag>())
        tagsAutoCompleteTextView.setAdapter(tagsAdapter)

        initTagSearch()

        tagsAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            addTag(position)
        }

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionEditViewModel::class.java)

        viewModel.getQuestionEditForm(questionId)

        viewModel.successfulEdit.observe(this, {
            if(it == true) {
                setResult(Constants.QUESTION_UPDATED)
                finish()
            }
        })

        viewModel.editError.observe(this, {
            handleEditError(it)
        })

        viewModel.editData.observe(this, {
            setQuestionData(it)
        })

        viewModel.getEditDataError.observe(this, {
            handleGetEditDataError(it)
        })

        viewModel.titleErrorMessage.observe(this, {
            titleErrorMessageText.visibility = View.VISIBLE
            titleErrorMessageText.text = it
        })

        viewModel.bodyErrorMessage.observe(this, {
            bodyErrorMessageText.visibility = View.VISIBLE
            bodyErrorMessageText.text = it
        })

        viewModel.validationError.observe(this, {
            if(it == true) {
                Snackbar.make(rootLayout, "Nevyplnili ste správne všetky polia", Snackbar.LENGTH_LONG)
                    .show()
            }
        })

        viewModel.tagsList.observe(this, {
            Log.d("Tags results size", it.tags.size.toString())
            tagsAdapter.clear()
            tagsAdapter.addAll(it.tags)
            tagsAdapter.notifyDataSetChanged()
            tagsAdapter.filter.filter(tagsAutoCompleteTextView.text, tagsAutoCompleteTextView)
        })

        viewModel.errorTagsList.observe(this, {
            handleTagsError(it)
        })
    }

    private fun initTagSearch() {
        tagsAutoCompleteTextView.doAfterTextChanged { text ->
            if(text != null && text.trim().length > 1) {
                Log.d("Tag query", text.toString())
                viewModel.searchQuery = text.toString()
                viewModel.getTagsList()
            } else {
                tagsAdapter.clear()
            }
        }
    }

    private fun addTag(position: Int) {
        val tag = tagsAdapter.getItem(position)
        Log.d("Selected tag", tag.toString())
        selectedTagsAdapter.addItem(tag!!)
        newTags.add(tag.id)
        clearTagField()
    }

    private fun clearTagField() {
        tagsAutoCompleteTextView.setText("")
        tagsAdapter.clear()
    }

    private fun editQuestion(){
        val questionEdit = QuestionEdit(
            questionId,
            questionTitleEditText?.text.toString(),
            questionBodyEditText?.text.toString(),
            newTags,
            deletedTags
        )

        bodyErrorMessageText.visibility = View.GONE
        titleErrorMessageText.visibility = View.GONE

        viewModel.editQuestion(questionId, questionEdit)
    }

    private fun setQuestionData(question: Question){
        questionTitleEditText?.setText(question.title)
        questionBodyEditText?.setText(question.body)
        selectedTagsAdapter.updateData(question.tags)
        originalTagsSize = question.tags.size
    }

    private fun handleGetEditDataError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Otázka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Nepodarilo sa načítať otázku", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.getQuestionEditForm(questionId)
                    }
                    .show()
            }
        }
    }

    private fun handleEditError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Otázka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            is ErrorEntity.AccessDenied -> {
                Snackbar.make(rootLayout, "Nemate práva na upravenie otázky", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Nepodarilo sa upraviť otázku", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        editQuestion()
                    }
                    .show()
            }
        }
    }

    private fun handleTagsError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(rootLayout, "Nepodarilo sa načítať tagy", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.getTagsList()
                    }
                    .show()
            }
        }
    }

    override fun removeTag(position: Int) {
        // if original tag has been removed
        if(position < originalTagsSize) {
            val tag: Tag = selectedTagsAdapter.getTag(position)
            deletedTags.add(tag.id)
            originalTagsSize--
        }

        // if new tag has been removed
        else {
            newTags.removeAt(position)
        }

        selectedTagsAdapter.removeItem(position)
    }
}