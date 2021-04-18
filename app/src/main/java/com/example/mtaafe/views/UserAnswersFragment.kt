package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.viewmodels.UserAnswersListViewModel
import com.example.mtaafe.viewmodels.UserQuestionsListViewModel
import com.google.android.material.snackbar.Snackbar

class UserAnswersFragment : Fragment() {
    private lateinit var viewModel: UserAnswersListViewModel
    private lateinit var adapter: UserAnswerAdapter
    private lateinit var userAnswersFragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userAnswersFragmentView = inflater.inflate(R.layout.user_answers_fragment, container, false)

        viewModel = activity?.let {
            ViewModelProvider.AndroidViewModelFactory(it.application)
                .create(UserAnswersListViewModel::class.java)
        }!!

        adapter = UserAnswerAdapter(ArrayList())

        val userAnswersListRecycler: RecyclerView = userAnswersFragmentView.findViewById(R.id.userAnswersListRecycler)
        userAnswersListRecycler.layoutManager = LinearLayoutManager(context)
        userAnswersListRecycler.adapter = adapter

        viewModel.getUserAnswersList()

        viewModel.userAnswersList.observe(this, {
            adapter.updateData(it.answers)
        })

        viewModel.error.observe(this, {
            handleError(it)
        })

        return userAnswersFragmentView
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(userAnswersFragmentView, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.getUserAnswersList()
                    }
                    .show()
            }
        }
    }
}