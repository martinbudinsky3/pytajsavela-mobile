package com.example.mtaafe.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.databinding.ActivityQuestionsListBinding
import com.example.mtaafe.viewmodels.QuestionsListViewModel

class QuestionsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionsListBinding
    private lateinit var viewModel: QuestionsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_list)
       val questionsListRecycler: RecyclerView = findViewById(R.id.questionsListRecycler)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_questions_list)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionsListViewModel::class.java)
        //binding.viewModel = viewModel
        //binding.lifecycleOwner = this


        questionsListRecycler.layoutManager = LinearLayoutManager(this)

        viewModel.getQuestionsList()

        fun handleError(error: ErrorEntity) {
            when(error) {
                is ErrorEntity.Unauthorized -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                else -> Toast.makeText(this, "Oops, niečo sa pokazilo. Vyskúšajte akciu neskôr prosím", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.result.observe(this, Observer {
            when(it) {
                is ApiResult.Success -> {
                    if(it.data is QuestionsList) {
                        questionsListRecycler.adapter = QuestionAdapter(it.data.questions)
                    }
                }
                is ApiResult.Error -> handleError(it.error)
                else -> {}
            }
        })
    }
}