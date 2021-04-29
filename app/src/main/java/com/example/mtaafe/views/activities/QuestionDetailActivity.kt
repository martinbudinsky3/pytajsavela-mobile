package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
import com.example.mtaafe.notifications.MyFirebaseMessagingService
import com.example.mtaafe.viewmodels.QuestionDetailViewModel
import com.example.mtaafe.views.adapters.AnswerAdapter
import com.example.mtaafe.views.adapters.ImageAdapter
import com.example.mtaafe.views.adapters.TagAdapter
import com.example.mtaafe.views.viewholders.AnswerViewHolder
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class QuestionDetailActivity: AppCompatActivity(), OnAnswerClickListener {
    private lateinit var viewModel: QuestionDetailViewModel
    private lateinit var answerAdapter: AnswerAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var rootLayout: View
    private lateinit var answersListRecycler: RecyclerView
    private lateinit var imagesListRecycler: RecyclerView
    private lateinit var questionTitleTextView: TextView
    private lateinit var questionBodyTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var createdAtTextView: TextView
    private lateinit var answersCountTextView: TextView
    private lateinit var answerButton : FloatingActionButton
    private lateinit var deleteButton : ImageButton
    private lateinit var editButton : ImageButton
    private lateinit var question : Question
    private var questionId: Long = 0
    private var answerToDelete: Long = 0
    private var questionImagesLoaded: Int = 0
    private var answersImagesLoaded: Int = 0
    private var shouldGetImages = true
    private var answersCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_detail)

        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""

        rootLayout = findViewById(R.id.questionDetailRoot)
        answersListRecycler = findViewById(R.id.answersListRecycler)
        imagesListRecycler = findViewById(R.id.imagesRecyclerView)
        answerButton = findViewById(R.id.answerBtn)
        deleteButton = findViewById(R.id.deleteBtn)
        editButton = findViewById(R.id.editBtn)

        deleteButton.visibility = View.GONE
        editButton.visibility = View.GONE

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionDetailViewModel::class.java)

        answerAdapter = AnswerAdapter(ArrayList(), viewModel.sessionManager?.fetchUserId()!!)
        answersListRecycler.layoutManager = LinearLayoutManager(this)
        answersListRecycler.adapter = answerAdapter

        imageAdapter = ImageAdapter(ArrayList())
        imagesListRecycler.layoutManager = LinearLayoutManager(this)
        imagesListRecycler.adapter = imageAdapter

        viewModel.getQuestionDetails(questionId)

        viewModel.question.observe(this, Observer {
            question = it
            setQuestionData()
            checkIfAuthor(question.author)

            if(shouldGetImages) {
                getQuestionImages(question.images)
            }

            getAnswersImages(question.answers)
        })

        viewModel.questionImages.observe(this, {
            it.subList(questionImagesLoaded, it.size).forEach{image -> imageAdapter.addItem(image.index, image.bitmap)}
            questionImagesLoaded = it.size
        })

        viewModel.answersImages.observe(this, {
            it.subList(answersImagesLoaded, it.size).forEach{image -> showAnswersImage(image)}
            answersImagesLoaded = it.size
        })

        MyFirebaseMessagingService.newAnswerId.observe(this, {
            if(it.questionId == questionId) {
                Log.d("QuestionDetailActivity", "Just got info about new answer")
                viewModel.getAnswer(it.id)
                MyFirebaseMessagingService.newAnswerId.value = NewAnswer(0L, 0L)
                setResult(Constants.QUESTION_UPDATED)
            }
        })

        viewModel.newAnswer.observe(this, {
            answerAdapter.appendAnswer(it)
            answersCount++
            answersCountTextView.text = "Odpovede (" + answersCount.toString() + "):"
        })

        viewModel.successfulQuestionDelete.observe(this, {
            if(it == true) {
                setResult(Constants.QUESTION_DELETED)
                finish()
            }
        })

        viewModel.successfulAnswerDelete.observe(this, {
            if(it == true) {
                setResult(Constants.QUESTION_UPDATED)
                answerAdapter.deleteAnswer(answerToDelete)
                answersCount--
                answersCountTextView.text = "Odpovede (" + answersCount.toString() + "):"
                showInfoSnackbar("Odpoveď bola odstránená")
            }
        })

        viewModel.questionError.observe(this, {
            handleQuestionError(it)
        })

        viewModel.questionDeleteError.observe(this, {
            handleQuestionDeleteError(it)
        })

        viewModel.answerDeleteError.observe(this, {
            handleAnswerDeleteError(it)
        })

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

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val newQuestionId = intent?.getLongExtra("question_id", 0)
//
//        if(newQuestionId != questionId) {
//            reload(newQuestionId!!)
//        }
//    }

    override fun onBackPressed() {
        if(isTaskRoot) {
            val intent = Intent(this, QuestionsListActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            super.onBackPressed()
        }
    }

    private fun reload(newQuestionId: Long) {
        questionId = newQuestionId
        answerToDelete = 0
        questionImagesLoaded = 0
        answersImagesLoaded = 0
        shouldGetImages = true
        answersCount = 0
        viewModel.getQuestionDetails(questionId)
    }

    private fun openDeleteDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)

        with(builder) {
            setTitle("Naozaj chcete odstrániť otázku?")
            setPositiveButton("Áno") { dialog, which ->
                viewModel.deleteQuestion(questionId)
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
                answerToDelete = answerId
                viewModel.deleteAnswer(answerId)
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

    private fun setQuestionData(){
        questionTitleTextView = findViewById(R.id.questionTitleTV)
        questionBodyTextView = findViewById(R.id.questionBodyTV)
        authorTextView = findViewById(R.id.authorTV)
        createdAtTextView = findViewById(R.id.createdAtTV)
        answersCountTextView = findViewById(R.id.answersCountTV)

        questionTitleTextView.text = question.title
        questionBodyTextView.text = question.body
        authorTextView.text = question.author.name
        createdAtTextView.text = question.createdAt.toString()
        answersCountTextView.text = "Odpovede (" + question.answers.size.toString() + "):"

        answersCount = question.answers.size

        answerAdapter.updateData(question.answers)

        val tagsRecyclerView: RecyclerView = findViewById(R.id.tagsQdetailRecyclerView)
        val adapter = TagAdapter(question.tags)
        val layoutManager = FlexboxLayoutManager(this)

        tagsRecyclerView.layoutManager = layoutManager
        tagsRecyclerView.adapter = adapter
    }

    private fun getQuestionImages(images: ArrayList<Image>) {
        imageAdapter.updateSize(images.size)
        images.forEachIndexed { index, image -> viewModel.getQuestionImage(image.id, index) }
    }

    private fun getAnswersImages(answers: ArrayList<Answer>) {
        answers.forEachIndexed { answerIndex, answer ->
            answer.images.forEachIndexed { imageIndex, image ->
                viewModel.getAnswerImage(
                    image.id,
                    imageIndex,
                    answerIndex
                )
            }
        }
    }

    private fun showAnswersImage(answersDecodedImage: AnswersDecodedImage) {
        val answerViewHolder: AnswerViewHolder =
            answersListRecycler.findViewHolderForAdapterPosition(answersDecodedImage.answerIndex) as AnswerViewHolder
        answerViewHolder.showImage(answersDecodedImage)
    }

    private fun handleQuestionError(error: ErrorEntity) {
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
                Snackbar.make(rootLayout, "Nepodarilo sa načítať otázku", Snackbar.LENGTH_LONG)
                        .setAction("Skúsiť znovu") {
                            viewModel.getQuestionDetails(questionId)
                        }
                        .show()
            }
        }
    }

    private fun handleQuestionDeleteError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.AccessDenied -> {
                Snackbar.make(rootLayout, "Nemáte právo na odstránenie otázky", Snackbar.LENGTH_LONG)
                    .show()
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Otázka neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Nepodarilo sa odstrániť otázku", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.deleteQuestion(questionId)
                    }
                    .show()
            }
        }
    }

    private fun handleAnswerDeleteError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.AccessDenied -> {
                Snackbar.make(rootLayout, "Nemáte právo na odstránenie odpovedi", Snackbar.LENGTH_LONG)
                    .show()
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(rootLayout, "Odpoveď neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(rootLayout, "Nemáte právo na odstránenie odpovedi", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.deleteAnswer(answerToDelete)
                    }
                    .show()
            }
        }
    }

    override fun onClickDeleteAnswer(position: Int) {
        val clickedAnswer: Answer = answerAdapter.getAnswer(position)
        Log.d("qes", "Question with id = " + clickedAnswer.id + " was clicked to delete!")
        openDeleteAnswerDialog(clickedAnswer.id)
    }

    override fun onClickEditAnswer(position: Int) {
        val clickedAnswer: Answer = answerAdapter.getAnswer(position)
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
                shouldGetImages = false
                viewModel.getQuestionDetails(questionId)
                setResult(Constants.QUESTION_UPDATED)
                showInfoSnackbar("Otázka bola upravená")
            }

            if(resultCode == Constants.ANSWER_CREATED) {
                shouldGetImages = false
                viewModel.getQuestionDetails(questionId)
                setResult(Constants.QUESTION_UPDATED)
                showInfoSnackbar("Odpoveď bola pridaná")
            }

            if(resultCode == Constants.ANSWER_UPDATED) {
                shouldGetImages = false
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