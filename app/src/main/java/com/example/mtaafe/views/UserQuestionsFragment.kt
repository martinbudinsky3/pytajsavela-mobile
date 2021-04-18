package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.databinding.UserQuestionsFragmentBinding
import com.example.mtaafe.viewmodels.UserInfoViewModel
import com.example.mtaafe.viewmodels.UserQuestionsListViewModel
import com.google.android.material.snackbar.Snackbar

class UserQuestionsFragment: Fragment() {
    //lateinit var binding: UserQuestionsFragmentBinding
    private lateinit var viewModel: UserQuestionsListViewModel
    private lateinit var adapter: QuestionsAdapterWithoutPagination
    private lateinit var userQuestionsFragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        binding = DataBindingUtil.inflate(inflater, R.layout.user_questions_fragment,
//            container, false)
        userQuestionsFragmentView = inflater.inflate(R.layout.user_questions_fragment, container, false)

        viewModel = activity?.let {
            ViewModelProvider.AndroidViewModelFactory(it.application)
                .create(UserQuestionsListViewModel::class.java)
        }!!

        adapter = QuestionsAdapterWithoutPagination(ArrayList())

//        binding.userQuestionsListRecycler.layoutManager = LinearLayoutManager(context)
//        binding.userQuestionsListRecycler.adapter = adapter
        val userQuestionsListRecycler: RecyclerView = userQuestionsFragmentView.findViewById(R.id.userQuestionsListRecycler)
        userQuestionsListRecycler.layoutManager = LinearLayoutManager(context)
        userQuestionsListRecycler.adapter = adapter

        viewModel.getUserQuestionsList()

        viewModel.userQuestionsList.observe(this, {
            adapter.updateData(it.questions)
        })

        viewModel.error.observe(this, {
            handleError(it)
        })

        //return binding.root
        return userQuestionsFragmentView
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(userQuestionsFragmentView, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.getUserQuestionsList()
                    }
                    .show()
            }
        }
    }
}