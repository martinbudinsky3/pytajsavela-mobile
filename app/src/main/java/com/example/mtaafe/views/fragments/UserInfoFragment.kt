package com.example.mtaafe.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.R
import com.example.mtaafe.data.models.ErrorEntity
import com.example.mtaafe.databinding.UserInfoFragmentBinding
import com.example.mtaafe.viewmodels.UserProfileViewModel
import com.example.mtaafe.views.activities.LoginActivity
import com.google.android.material.snackbar.Snackbar

class UserInfoFragment: Fragment() {
    lateinit var binding: UserInfoFragmentBinding
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_info_fragment,
                container, false)

        viewModel = activity?.let { ViewModelProvider(it).get(UserProfileViewModel::class.java) }!!

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getUserInfo()

        viewModel.errorInfo.observe(this, {
            handleError(it)
        })

        return binding.root
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
                Snackbar.make(binding.root, "Nepodarilo sa načítať informácie", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Skúsiť znovu") {
                        viewModel.getUserInfo()
                    }
                    .show()
            }
        }
    }
}