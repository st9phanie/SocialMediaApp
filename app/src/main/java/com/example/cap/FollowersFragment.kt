package com.example.cap

import SearchResultsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FollowersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore
    private val userList = mutableListOf<SearchResultProfile>()
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        db = FirebaseFirestore.getInstance()
        val uid = arguments?.getString("uid") ?: return
        userId = uid

        searchResultsAdapter = SearchResultsAdapter(userList) { selectedUser ->
            openUserProfile(selectedUser)
        }
        recyclerView.adapter = searchResultsAdapter

        loadFollowers()
    }


    fun loadFollowers() {
        userId?.let {
            db.collection("user_profile_info").document(it).get()
                .addOnSuccessListener { document ->
                    userList.clear()
                    val followerIds = document.get("followers") as? List<String> ?: emptyList()
                    if (followerIds.isEmpty()) {
                        searchResultsAdapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }

                    for (followerId in followerIds) {
                        db.collection("user_profile_info").document(followerId).get()
                            .addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(SearchResultProfile::class.java)
                                if (user != null) {
                                    userList.add(user)
                                    searchResultsAdapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
        }
        }

    private fun openUserProfile(user: SearchResultProfile) {
        val fragment = OthersProfile()
        val bundle = Bundle().apply {
            putString("uid", user.uid)
            putString("username", user.username)
            putString("displayName", user.display_name)
        }
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
