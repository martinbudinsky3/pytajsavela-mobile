package com.example.mtaafe.views

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.databinding.ActivityQuestionsListBinding
import com.example.mtaafe.viewmodels.QuestionsListViewModel

class QuestionsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionsListBinding
    private lateinit var viewModel: QuestionsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_list)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_questions_list)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionsListViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getQuestionsList()
    }
}