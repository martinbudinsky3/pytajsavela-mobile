package com.example.mtaafe.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ApiResult
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.data.models.TagsList
import com.example.mtaafe.data.models.User
import com.example.mtaafe.databinding.ActivityLoginBinding
import com.example.mtaafe.databinding.UserInfoFragmentBinding
import com.example.mtaafe.viewmodels.LoginViewModel
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.example.mtaafe.viewmodels.UserInfoViewModel
import com.google.android.material.snackbar.Snackbar

class UserInfoFragment: Fragment() {
    lateinit var binding: UserInfoFragmentBinding
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userInfoFragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userInfoFragmentView = inflater.inflate(R.layout.user_info_fragment, container, false)
        //binding = DataBindingUtil.inflate(inflater, R.layout.user_info_fragment,
        //        container, false)

        userNameText = userInfoFragmentView.findViewById(R.id.userNameText)
        userEmailText = userInfoFragmentView.findViewById(R.id.userEmailText)

        viewModel = activity?.let {
            ViewModelProvider.AndroidViewModelFactory(it.application)
                .create(UserInfoViewModel::class.java)
        }!!

        viewModel.getUserInfo()

        viewModel.user.observe(this, {
            userNameText.text = it.name
            userEmailText.text = it.email
        })

        viewModel.error.observe(this, {
            handleError(it)
        })

        //return binding.root
        return userInfoFragmentView
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(userInfoFragmentView, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        Log.d("Test SNACKBAR", "Test")
                        viewModel.getUserInfo()
                    }
                    .show()
            }
        }
    }
}