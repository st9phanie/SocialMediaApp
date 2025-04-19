package com.example.cap

import SearchResultsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FollowingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var userId: String? = null
    private val userList = mutableListOf<SearchResultProfile>()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val uid = arguments?.getString("uid") ?: return
        userId = uid

        searchResultsAdapter = SearchResultsAdapter(userList) { selectedUser ->
            openUserProfile(selectedUser)
        }
        recyclerView.adapter = searchResultsAdapter

        loadFollowing()
    }


    fun loadFollowing() {
        userId?.let {
            db.collection("user_profile_info").document(it).get()
                .addOnSuccessListener { document ->
                    userList.clear()
                    val followingIds = document.get("following") as? List<String> ?: emptyList()
                    if (followingIds.isEmpty()) {
                        searchResultsAdapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }

                    for (followingId in followingIds) {
                        db.collection("user_profile_info").document(followingId).get()
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
