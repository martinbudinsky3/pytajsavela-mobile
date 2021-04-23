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
import com.example.mtaafe.views.adapters.UserAnswerAdapter
import com.example.mtaafe.views.activities.LoginActivity
import com.google.android.material.snackbar.Snackbar

class UserAnswersFragment : Fragment() {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var userAnswersListRecycler: RecyclerView
    private lateinit var emptyUserAnswersListText: TextView
    private lateinit var adapter: UserAnswerAdapter
    private lateinit var userAnswersFragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userAnswersFragmentView = inflater.inflate(R.layout.user_answers_fragment, container, false)

        userAnswersListRecycler = userAnswersFragmentView.findViewById(R.id.userAnswersListRecycler)
        emptyUserAnswersListText = userAnswersFragmentView.findViewById(R.id.emptyUserAnswersListText)

        viewModel = activity?.let { ViewModelProvider(it).get(UserProfileViewModel::class.java) }!!

        adapter = UserAnswerAdapter(ArrayList())

        userAnswersListRecycler.layoutManager = LinearLayoutManager(context)
        userAnswersListRecycler.adapter = adapter

        viewModel.getUserAnswersList()

        viewModel.userAnswersList.observe(this, {
            if(it.answers.isNotEmpty()) {
                hideEmptyListMessage()
                adapter.updateData(it.answers)
            } else {
                showEmptyListMessage()
            }
        })

        viewModel.errorAnswers.observe(this, {
            handleError(it)
        })

        return userAnswersFragmentView
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(userAnswersFragmentView, "Používateľ neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        activity?.finish()
                    }
                    .show()
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

    private fun showEmptyListMessage() {
        emptyUserAnswersListText.visibility = View.VISIBLE
        userAnswersListRecycler.visibility = View.GONE
    }

    private fun hideEmptyListMessage() {
        emptyUserAnswersListText.visibility = View.GONE
        userAnswersListRecycler.visibility = View.VISIBLE
    }
}