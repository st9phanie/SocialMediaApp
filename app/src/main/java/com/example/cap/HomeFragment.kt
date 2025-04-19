package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerViewState: Parcelable? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String
    private var posts = mutableListOf<Post>()
    private lateinit var displayNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private var lastFeedLoadTime: Long = 0L
    private val feedRefreshInterval = 2 * 60 * 1000L
    private lateinit var dms: ImageButton

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        displayNameTextView = view.findViewById(R.id.displayName)
        usernameTextView = view.findViewById(R.id.username)
        dms = view.findViewById(R.id.dms)

        dms.setOnClickListener {
            val intent = Intent(requireContext(), DirectMessages::class.java)
            startActivity(intent)
        }

        currentUserId = auth.currentUser?.uid.toString()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(posts){ userId,username,displayName ->
            openUserProfile(userId,username, displayName)
        }
        recyclerView.adapter = postAdapter

        loadHomePage()
    }

    private fun getFollowingList(callback: (List<String>) -> Unit) {
        FirebaseFirestore.getInstance().collection("user_profile_info").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    val followingList = document.get("following") as? List<String> ?: emptyList()
                    callback(followingList)
                } else {
                    callback(emptyList())
                }
            }
    }
    private fun openUserProfile(userId: String,username: String,displayName: String) {
        val fragment = OthersProfile()
        val bundle = Bundle()
        bundle.putString("uid", userId)
        bundle.putString("username", username)
        bundle.putString("displayName", displayName)
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadHomePage() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFeedLoadTime < feedRefreshInterval) return

        lastFeedLoadTime = currentTime
        getFollowingList { followingUsers ->
            if (followingUsers.isEmpty()) {
                Log.d("Feed", "No users followed, showing empty feed.")
                posts.clear()
                postAdapter.notifyDataSetChanged()
                return@getFollowingList
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("text_post")
                .whereIn("uid", followingUsers)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val fetchedPosts = mutableListOf<Post>()
                    val userIdsToFetch = mutableSetOf<String>()

                    snapshot?.documents?.forEach { doc ->
                        doc.toObject(Post::class.java)?.let { post ->
                            fetchedPosts.add(post)
                            userIdsToFetch.add(post.uid)
                        }
                    }

                    if (userIdsToFetch.isEmpty()) {
                        posts.clear()
                        posts.addAll(fetchedPosts)
                        postAdapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }

                    db.collection("user_profile_info")
                        .whereIn("uid", userIdsToFetch.toList())
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val userMap = userSnapshot.documents.associateBy { it.getString("uid") }

                            fetchedPosts.forEach { post ->
                                val userDoc = userMap[post.uid]
                                post.displayName = userDoc?.getString("display_name").orEmpty()
                                post.username = userDoc?.getString("username").orEmpty()


                            }

                            posts.clear()
                            posts.addAll(fetchedPosts)
                            postAdapter.notifyDataSetChanged()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("Feed", "Error fetching posts", e)
                }
        }
    }

}

