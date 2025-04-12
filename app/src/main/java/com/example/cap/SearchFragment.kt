package com.example.cap

import SearchResultsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchView: SearchView
    private var currentUsername: String? = null
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<SearchResultProfile>()
    private lateinit var searchResultsAdapter: SearchResultsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            currentUsername = it.getString("username") } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_search, container, false) }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchResultsAdapter = SearchResultsAdapter(userList) { selectedUser ->
            openUserProfile(selectedUser) }
        recyclerView.adapter = searchResultsAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle when user submits the query (optional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchForUser(newText.trim())
                }
                else{
                    userList.clear()
                }
                return true
            }
        })
    }

    private fun searchForUser(username: String) {
        db.collection("user_profile_info")
            .whereGreaterThanOrEqualTo("username", username)
            .whereLessThanOrEqualTo("username", username + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                } else {
                    userList.clear()
                    for (document in documents) {
                        val userProfile = document.toObject(SearchResultProfile::class.java)
                        userProfile.display_name = document.getString("display_name") ?: ""
                        if (userProfile.username != currentUsername) {
                            userList.add(userProfile)
                        }

                    // Notify the adapter that data has changed so the RecyclerView will update
                    searchResultsAdapter.notifyDataSetChanged()
                }
            }
    }}

    private fun openUserProfile(user: SearchResultProfile) {
        val fragment = OthersProfile()
        val bundle = Bundle()
        bundle.putString("uid", user.uid)
        bundle.putString("username", user.username)
        bundle.putString("displayName", user.display_name)
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }



}
