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

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchView: SearchView
    private lateinit var currentUsername: String
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<SearchResultProfile>()
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
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

        // Initialize adapter
        searchResultsAdapter = SearchResultsAdapter(userList) { selectedUser ->
            openUserProfile(selectedUser)
        }
        recyclerView.adapter = searchResultsAdapter

        // Set the query text listener on the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle when user submits the query (optional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Perform the search when text is changed in the SearchView
                if (!newText.isNullOrEmpty()) {
                    searchForUser(newText.trim())
                }
                return true
            }
        })
    }

    private fun searchForUser(username: String) {
        db.collection("users")
            .whereNotEqualTo("username",currentUsername)
            .whereGreaterThanOrEqualTo("username", username)
            .whereLessThanOrEqualTo("username", username + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No user found with the given substring
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                } else {
                    // Users found, update RecyclerView with the results
                    userList.clear() // Clear previous results
                    for (document in documents) {
                        val userProfile = document.toObject(SearchResultProfile::class.java)
                        userList.add(userProfile)
                    }

                    // Notify the adapter that data has changed so the RecyclerView will update
                    searchResultsAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun openUserProfile(user: SearchResultProfile) {
        val fragment = OthersProfile()
        val bundle = Bundle()
        bundle.putString("uid", user.uid)
        bundle.putString("username", user.username)
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment) // Replace with your actual container ID
            .addToBackStack(null)
            .commit()
    }



}
