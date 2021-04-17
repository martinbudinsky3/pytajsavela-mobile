package com.example.mtaafe.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class UserProfileViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private val userProfileFragments: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        userProfileFragments.add(fragment);
        notifyDataSetChanged();
    }

    override fun getCount(): Int {
        return userProfileFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return userProfileFragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(position == 0) return "Údaje"
        if(position == 1) return "Otázky"
        return "Odpovede"
    }
}