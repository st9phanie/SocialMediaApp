package com.example.cap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OthersProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class OthersProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var userId: String? = null
    private var username: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var followBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var displayNameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var posts = mutableListOf<Post>()
    private lateinit var postsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            userId = it.getString("uid")  // Get UID
            username = it.getString("username")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OthersProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OthersProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        followBtn = view.findViewById(R.id.follow_btn)
        displayNameTextView = view.findViewById(R.id.displayName)
        bioTextView = view.findViewById(R.id.bio)
        usernameTextView = view.findViewById(R.id.username)
        postsCount = view.findViewById(R.id.post_count)
        backBtn = view.findViewById(R.id.backBtn)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameTextView.text = username
/*
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    for (document in task.result) {
                        val userId = document.id // UID is the document ID
                        Log.d("Firestore", "User ID: $userId")
                    }
                }
            }*/

        userId?.let { loadUserProfile(it) }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
        if (userId != null) {
            postsCount.text = loadUserTweets(userId!!) }

        backBtn.setOnClickListener {
                val fragment = SearchFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            }

        followBtn.setOnClickListener {

        }
        }

    private fun followUser(){

    }

    private fun loadUserProfile(userId: String) {
        val usernameTextView = view?.findViewById<TextView>(R.id.username)
        val displayNameTextView = view?.findViewById<TextView>(R.id.displayName)
        val bioTextView = view?.findViewById<TextView>(R.id.bio)
        val postCount = view?.findViewById<TextView>(R.id.post_count)

        db.collection("user_profile_info").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val displayName = document.getString("display_name") ?: ""
                    val bio = document.getString("bio") ?: ""
                    val username = username ?: "No Username"
                    if (usernameTextView != null) {
                        usernameTextView.text = username
                    }
                    if (displayNameTextView != null) {
                        displayNameTextView.text = displayName
                    }
                    if (bioTextView != null) {
                        bioTextView.text = bio
                    }
                }
            }
    }

    private fun loadUserTweets(userId: String): String {
        db.collection("text_post")
            .whereEqualTo("uid", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    posts.clear()
                    for (document in snapshot.documents) {
                        val tweet = document.toObject(Post::class.java)

                        if (tweet != null) {
                            tweet.displayName = displayNameTextView.text.toString()
                            tweet.username = usernameTextView.text.toString()
                            posts.add(tweet)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        return posts.size.toString()}
}
