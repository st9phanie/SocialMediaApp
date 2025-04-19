package com.example.cap

import SearchResultsAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NewChat : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private var currentUsername: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var backBtn: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<SearchResultProfile>()
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_chat)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        searchView = findViewById(R.id.searchView)
        backBtn = findViewById(R.id.backBtn)
        currentUsername = sharedPref.getString("username","")
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchResultsAdapter = SearchResultsAdapter(userList) { selectedUser -> openUserProfile(selectedUser) }
        recyclerView.adapter = searchResultsAdapter
        backBtn.setOnClickListener {
            finish() }
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
                    Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this, ChannelActivity::class.java).apply {
            putExtra("uid", user.uid)
            putExtra("username", user.username)
        }
        startActivity(intent)
    }

}