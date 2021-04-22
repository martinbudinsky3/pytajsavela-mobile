package com.example.mtaafe.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.viewmodels.UserProfileViewModel
import com.example.mtaafe.views.adapters.QuestionsAdapterWithoutPagination
import com.example.mtaafe.views.activities.LoginActivity
import com.google.android.material.snackbar.Snackbar

class UserQuestionsFragment: Fragment() {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var userQuestionsListRecycler: RecyclerView
    private lateinit var emptyUserQuestionsListText: TextView
    private lateinit var adapter: QuestionsAdapterWithoutPagination
    private lateinit var userQuestionsFragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userQuestionsFragmentView = inflater.inflate(R.layout.user_questions_fragment, container, false)

        userQuestionsListRecycler= userQuestionsFragmentView.findViewById(R.id.userQuestionsListRecycler)
        emptyUserQuestionsListText = userQuestionsFragmentView.findViewById(R.id.emptyUserQuestionsListText)

        viewModel = activity?.let {
            ViewModelProvider.AndroidViewModelFactory(it.application)
                .create(UserProfileViewModel::class.java)
        }!!

        adapter = QuestionsAdapterWithoutPagination(ArrayList())

        userQuestionsListRecycler.layoutManager = LinearLayoutManager(context)
        userQuestionsListRecycler.adapter = adapter

        viewModel.getUserQuestionsList()

        viewModel.userQuestionsList.observe(this, {
            if(it.questions.isNotEmpty()) {
                hideEmptyListMessage()
                adapter.updateData(it.questions)
            } else {
                showEmptyListMessage()
            }
        })

        viewModel.errorQuestions.observe(this, {
            handleError(it)
        })

        return userQuestionsFragmentView
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(userQuestionsFragmentView, "Používateľ neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        activity?.finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(userQuestionsFragmentView, "Nepodarilo sa načítať otázky", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        activity?.finish()
                        viewModel.getUserQuestionsList()
                    }
                    .show()
            }
        }
    }

    private fun showEmptyListMessage() {
        emptyUserQuestionsListText.visibility = View.VISIBLE
        userQuestionsListRecycler.visibility = View.GONE
    }

    private fun hideEmptyListMessage() {
        emptyUserQuestionsListText.visibility = View.GONE
        userQuestionsListRecycler.visibility = View.VISIBLE
    }
}