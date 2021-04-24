package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionDetailViewModel
import com.example.mtaafe.views.adapters.AnswerAdapter
import com.example.mtaafe.views.adapters.ImageAdapter
import com.example.mtaafe.views.adapters.TagAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class QuestionDetailActivity: AppCompatActivity(), OnAnswerClickListener {
    private lateinit var viewModel: QuestionDetailViewModel
    private lateinit var rootLayout: View
    private lateinit var answersListRecycler: RecyclerView
    private lateinit var imagesListRecycler: RecyclerView
    private lateinit var answerAdapter: AnswerAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var deleteButton : ImageButton
    private lateinit var editButton : ImageButton
    private lateinit var question : Question
    private var questionId: Long = 0
    private var questionImagesLoaded: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_detail)

        questionId = intent.getLongExtra("question_id", 0)

        rootLayout = findViewById(R.id.questionDetailRoot)
        answersListRecycler = findViewById(R.id.answersListRecycler)
        imagesListRecycler = findViewById(R.id.imagesRecyclerView)
        deleteButton = findViewById(R.id.deleteBtn)
        editButton = findViewById(R.id.editBtn)

        deleteButton.visibility = View.INVISIBLE
        editButton.visibility = View.INVISIBLE

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionDetailViewModel::class.java)

        answerAdapter = AnswerAdapter(ArrayList(), viewModel.sessionManager?.fetchUserId()!!, this)
        answersListRecycler.layoutManager = LinearLayoutManager(this)
        answersListRecycler.adapter = answerAdapter

        imageAdapter = ImageAdapter(ArrayList())
        imagesListRecycler.layoutManager = LinearLayoutManager(this)
        imagesListRecycler.adapter = imageAdapter

        viewModel.getQuestionDetails(questionId)

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is Question) {
                        question = it.data
                        setQuestionData(question)
                        checkIfAuthor(question.author)
                        getQuestionImages(question.images)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })

        viewModel.questionImages.observe(this, {
            it.subList(questionImagesLoaded, it.size).forEach{image -> imageAdapter.addItem(image)}
            questionImagesLoaded = it.size
        })

        val answerButton : FloatingActionButton = findViewById(R.id.answerBtn)

        answerButton.setOnClickListener {
            val intent = Intent(this, AnswerFormActivity::class.java)
            intent.putExtra("question_id", questionId)
            startActivityForResult(intent, Constants.UPDATE_UI)
        }

        deleteButton.setOnClickListener {
            openDeleteDialog()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, QuestionEditActivity::class.java)
            intent.putExtra("question_id", questionId)
            startActivityForResult(intent, Constants.UPDATE_UI)
        }
    }

    private fun openDeleteDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)

        with(builder) {
            setTitle("Naozaj chcete odstrániť otázku?")
            setPositiveButton("Áno") { dialog, which ->
                viewModel.deleteQuestion(questionId)
                setResult(Constants.QUESTION_DELETED)
                finish()
            }

            setNegativeButton("Nie"){dialog, which ->
                Log.d("no", "Option NO was clicked!")
            }

            setView(dialogLayout)
            show()
        }
    }

    private fun openDeleteAnswerDialog(answerId: Long){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)

        with(builder) {
            setTitle("Naozaj chcete odstrániť odpoveď?")
            setPositiveButton("Áno"){ dialog, which ->
                viewModel.deleteAnswer(answerId)
                setResult(Constants.QUESTION_UPDATED)
                showInfoSnackbar("Odpoveď bola odstránená")
                // TODO remove answer from recycler
            }

            setNegativeButton("Nie"){dialog, which ->
                Log.d("no", "Option NO was clicked!")
            }

            setView(dialogLayout)
            show()
        }
    }

    private fun checkIfAuthor(author: User){
        if (author.id == viewModel.sessionManager?.fetchUserId()){
            deleteButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
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
        answersCountTV.text = "Odpovede (" + question.answers.size.toString() + "):"

        answerAdapter.updateData(question.answers)

        val tagsRecyclerView: RecyclerView = findViewById(R.id.tagsQdetailRecyclerView)
        val adapter = TagAdapter(question.tags)
        val layoutManager = FlexboxLayoutManager(this)

        tagsRecyclerView.layoutManager = layoutManager
        tagsRecyclerView.adapter = adapter
    }

    private fun getQuestionImages(images: ArrayList<Image>) {
        imageAdapter.updateSize(images.size)
        val imagesIds: List<Long> = images.map { image -> image.id }
        imagesIds.forEachIndexed { index, id -> viewModel.getQuestionImage(id, index) }
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.AccessDenied -> {
                Snackbar.make(rootLayout, "Na danú akciu nemate práva", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Daná položka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
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

    override fun onClickDeleteAnswer(position: Int) {
        val clickedAnswer: AnswerItem = answerAdapter.getAnswer(position)
        Log.d("qes", "Question with id = " + clickedAnswer.id + " was clicked to delete!")
        openDeleteAnswerDialog(clickedAnswer.id)
    }

    override fun onClickEditAnswer(position: Int) {
        val clickedAnswer: AnswerItem = answerAdapter.getAnswer(position)
        Log.d("qes", "Question with id = " + clickedAnswer.id + " was clicked to edit!")

        val intent = Intent(this, AnswerEditActivity::class.java)
        intent.putExtra("answer_id", clickedAnswer.id)
        intent.putExtra("question_id", questionId)
        startActivityForResult(intent, Constants.UPDATE_UI)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("QuestionDetailActivity", "$requestCode $resultCode")

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.UPDATE_UI) {
            if(resultCode == Constants.QUESTION_UPDATED) {
                viewModel.getQuestionDetails(questionId)
                setResult(Constants.QUESTION_UPDATED)
                showInfoSnackbar("Otázka bola upravená")
            }

            if(resultCode == Constants.ANSWER_CREATED) {
                viewModel.getQuestionDetails(questionId)
                setResult(Constants.QUESTION_UPDATED)
                showInfoSnackbar("Odpoveď bola pridaná")
            }

            if(resultCode == Constants.ANSWER_UPDATED) {
                viewModel.getQuestionDetails(questionId)
                showInfoSnackbar("Odpoveď bola upravená")
            }
        }
    }

    private fun showInfoSnackbar(message: String) {
        Snackbar.make(
            rootLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}