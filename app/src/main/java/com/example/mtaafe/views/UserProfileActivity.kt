package com.example.mtaafe.views

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.mtaafe.R
import com.example.mtaafe.viewmodels.TagQuestionsListViewModel
import com.example.mtaafe.viewmodels.UserProfileViewModel
import com.google.android.material.tabs.TabLayout


class UserProfileActivity : AppCompatActivity() {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(UserProfileViewModel::class.java)

        val userId = intent.getLongExtra("userId", -1)
        if(userId == -1L) {
            viewModel.setLoggedInUserId()
        } else {
            viewModel.setUserId(userId)
        }

        supportActionBar?.title = "Profil"

        viewPager = findViewById(R.id.userProfileViewPager)
        tabLayout = findViewById(R.id.userProfileTabLayout)

        val userInfoFragment = UserInfoFragment()
        val userQuestionsFragment = UserQuestionsFragment()
        val userAnswersFragment = UserAnswersFragment()

        val pagerAdapter = UserProfileViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(userInfoFragment)
        pagerAdapter.addFragment(userQuestionsFragment)
        pagerAdapter.addFragment(userAnswersFragment)

        viewPager.adapter = pagerAdapter
    }
}