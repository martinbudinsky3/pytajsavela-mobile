package com.example.mtaafe.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.mtaafe.R
import com.example.mtaafe.viewmodels.UserProfileViewModel
import com.example.mtaafe.views.adapters.UserProfileViewPagerAdapter
import com.example.mtaafe.views.fragments.UserAnswersFragment
import com.example.mtaafe.views.fragments.UserInfoFragment
import com.example.mtaafe.views.fragments.UserQuestionsFragment
import com.google.android.material.tabs.TabLayout


class UserProfileActivity : DrawerActivity(), IQuestionDetailOpener {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user_profile)

        // inject activity_user_profile layout into drawer layout
        val dynamicContent: LinearLayout = findViewById(R.id.dynamicContent)
        val questionsListView: View = layoutInflater.inflate(R.layout.activity_user_profile, dynamicContent, false)
        dynamicContent.addView(questionsListView)

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

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    override fun openQuestionDetailActivity(questionId: Long) {
        val intent = Intent(this, QuestionDetailActivity::class.java)
        intent.putExtra("question_id", questionId)
        startActivity(intent)
    }
}