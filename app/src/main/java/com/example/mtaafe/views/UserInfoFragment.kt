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
import com.example.mtaafe.databinding.UserInfoFragmentBinding
import com.example.mtaafe.viewmodels.LoginViewModel
import com.example.mtaafe.viewmodels.QuestionsListViewModel
import com.example.mtaafe.viewmodels.UserInfoViewModel
import com.example.mtaafe.viewmodels.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar

class UserInfoFragment: Fragment() {
    lateinit var binding: UserInfoFragmentBinding
//    private lateinit var viewModel: UserInfoViewModel
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_info_fragment,
                container, false)

//        viewModel = activity?.let {
//            ViewModelProvider.AndroidViewModelFactory(it.application)
//                .create(UserInfoViewModel::class.java)
//        }!!
        viewModel = activity?.let {
            ViewModelProvider.AndroidViewModelFactory(it.application)
                .create(UserProfileViewModel::class.java)
        }!!

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getUserInfo()

//        viewModel.error.observe(this, {
//            handleError(it)
//        })
        viewModel.errorInfo.observe(this, {
            handleError(it)
        })

        return binding.root
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            is ErrorEntity.NotFound -> {
                Snackbar.make(binding.root, "Používateľ neexistuje", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Späť") {
                        activity?.finish()
                    }
                    .show()
            }
            else -> {
                Snackbar.make(binding.root, "Oops, niečo sa pokazilo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.getUserInfo()
                    }
                    .show()
            }
        }
    }
}