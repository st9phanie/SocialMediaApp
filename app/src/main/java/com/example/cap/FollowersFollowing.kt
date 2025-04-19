package com.example.cap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FollowersFollowing : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var backBtn: ImageButton
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)



        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followers_following, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backBtn = view.findViewById(R.id.backBtn)
        userId = auth.currentUser?.uid
        val defaultTabPosition = arguments?.getInt("tabPosition", 0) ?: 0
        val tabLayout = view.findViewById<TabLayout>(R.id.follow_tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        val pagerAdapter = FollAdapter(this,userId ?: "")
        viewPager.adapter = pagerAdapter

        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Followers" else "Following"
        }.attach()

        viewPager.currentItem = defaultTabPosition

    }


}
