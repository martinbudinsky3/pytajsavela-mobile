package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionDetailViewModel
import com.example.mtaafe.views.adapters.AnswerAdapter
import com.example.mtaafe.views.viewholders.OnAnswerClickListener
import com.example.mtaafe.views.adapters.TagAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class QuestionDetailActivity: AppCompatActivity(), OnAnswerClickListener {
    private lateinit var viewModel: QuestionDetailViewModel
    private lateinit var rootLayout: View
    private lateinit var answerAdapter: AnswerAdapter
//    private lateinit var imageAdapter: ImageAdapter
    private lateinit var deleteButton : Button
    private lateinit var editButton : Button
    private lateinit var question : Question
    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_detail)

        questionId = intent.getLongExtra("question_id", 0)

        val answersListRecycler: RecyclerView = findViewById(R.id.answersListRecycler)
//        val imagesRecyclerView: RecyclerView = findViewById(R.id.imagesRecyclerView)

        deleteButton = findViewById(R.id.deleteBtn)
        editButton = findViewById(R.id.editBtn)

        deleteButton.visibility = View.INVISIBLE
        editButton.visibility = View.INVISIBLE

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionDetailViewModel::class.java)


        answerAdapter = AnswerAdapter(ArrayList<AnswerItem>(), viewModel.sessionManager?.fetchUserId()!!, this)
        answersListRecycler.layoutManager = LinearLayoutManager(this)
        answersListRecycler.adapter = answerAdapter

//        imageAdapter = ImageAdapter(ArrayList<ByteArray>())
//        imagesRecyclerView.layoutManager = LinearLayoutManager(this)
//        imagesRecyclerView.adapter = imageAdapter

        viewModel.getQuestionDetails(questionId)

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is Question) {
                        question = it.data
                        setQuestionData(question)
                        checkIfAuthor(question.author)
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

        deleteButton.setOnClickListener {
            openDeleteDialog()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, QuestionEditActivity::class.java)
            intent.putExtra("question_id", questionId)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    private fun openDeleteDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)

        with(builder) {
            setTitle("Naozaj chceš odstrániť otázku?")
            setPositiveButton("Áno"){dialog, which ->
                viewModel.deleteQuestion(questionId)

//                changeActivity()
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
            setTitle("Naozaj chceš odstrániť odpoveď?")
            setPositiveButton("Áno"){dialog, which ->
                viewModel.deleteAnswer(answerId)

//                reloadActivity()
                recreate()
            }

            setNegativeButton("Nie"){dialog, which ->
                Log.d("no", "Option NO was clicked!")
            }

            setView(dialogLayout)
            show()
        }
    }

    private fun changeActivity(){
        val intent = Intent(this, QuestionsListActivity::class.java)
        startActivity(intent)
    }

    private fun reloadActivity(){
        val intent = Intent(this, this::class.java)
        intent.putExtra("question_id", questionId)
        startActivity(intent)
        finish()
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
        answersCountTV.text = ("Odpovede (" + question.answers.size.toString() + "):")

        val tagsQdetailRecyclerView: RecyclerView = findViewById(R.id.tagsQdetailRecyclerView)
        val adapter = TagAdapter(question.tags)
        val layoutManager = FlexboxLayoutManager(this)

        answerAdapter.updateData(question.answers)

//        for (image in question.images){
//            Log.d("msg", "IMAGE ID: $image")
//        }

//        val images: List<ByteArray> = getImagesContents(question.images)
//        imageAdapter.updateData(images)

        tagsQdetailRecyclerView.layoutManager = layoutManager
        tagsQdetailRecyclerView.adapter = adapter
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
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

    private fun getImagesContents(images: ArrayList<Image>) : List<ByteArray>{

        var imagesContents = mutableListOf<ByteArray>()

        for (image in images){
            viewModel.getImage(image.id)

            viewModel.result.observe(this, Observer {
                when(it) {
                    is ApiResult.Success -> {
                        if(it.data is ByteArray) {
                            imagesContents.add(it.data)
                        }
                    }
                    is ApiResult.Error -> handleError(it.error)
                    else -> {}
                }
            })
        }
        return imagesContents
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        startActivity(intent)
    }
}