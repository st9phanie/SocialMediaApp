package com.example.cap

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollAdapter(fragment: Fragment, private val userId: String) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> FollowersFragment()  // 0 = followers
            1 -> FollowingFragment()  // 1 = following
            else -> ProfileFragment() // fallback
        }

        fragment.arguments = Bundle().apply {
            putString("uid", userId)
        }
        return fragment
    }
}

