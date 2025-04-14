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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OthersProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var otherUserId: String? = null
    private var otherUsername: String? = null
    private var otherDisplayName: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var followBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var displayNameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserRef: DocumentReference
    private lateinit var currentUserId: String
    private lateinit var db: FirebaseFirestore
    private var posts = mutableListOf<Post>()
    private lateinit var postCount: TextView
    private lateinit var followerCountTextView: TextView
    private lateinit var followingCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            otherUserId = it.getString("uid")
            otherUsername = it.getString("username")
            otherDisplayName = it.getString("displayName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_others_profile, container, false) }

    companion object {
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
        postCount = view.findViewById(R.id.post_count)
        followerCountTextView = view.findViewById(R.id.follower_count)
        followingCountTextView = view.findViewById(R.id.following_count)
        backBtn = view.findViewById(R.id.backBtn)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        usernameTextView.text = otherUsername
        currentUserId = auth.currentUser?.uid.toString()
        currentUserRef = db.collection("user_profile_info").document(currentUserId)
        otherUserId?.let { loadUserProfile(it) }
        checkFollowStatus(otherUserId!!)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(posts) { userId, username, displayName -> }
        recyclerView.adapter = postAdapter
        otherUserId?.let { loadUserTweets(it) }

        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        followBtn.setOnClickListener {
            if (followBtn.text == "Follow") {
                otherUserId?.let { followUser(it) }
                followBtn.text = "Unfollow"
            } else {
                otherUserId?.let { unfollowUser(it) }
                followBtn.text = "Follow"
            }
        }
    }

        private fun followUser(otherUserId: String) {
            val targetUserRef = db.collection("user_profile_info").document(otherUserId)
            currentUserRef.update("following", FieldValue.arrayUnion(otherUserId))
            targetUserRef.update("followers", FieldValue.arrayUnion(currentUserId))
        }

        private fun unfollowUser(otherUserId: String) {
            val targetUserRef = db.collection("user_profile_info").document(otherUserId)
            currentUserRef.update("following", FieldValue.arrayRemove(otherUserId))
            targetUserRef.update("followers", FieldValue.arrayRemove(currentUserId))
        }


        private fun loadUserProfile(userId: String) {
        val usernameTextView = view?.findViewById<TextView>(R.id.username)
        val displayNameTextView = view?.findViewById<TextView>(R.id.displayName)
        val bioTextView = view?.findViewById<TextView>(R.id.bio)
        val postCount = view?.findViewById<TextView>(R.id.post_count)
        val userProfileRef = db.collection("user_profile_info").document(userId)
        val followingCountTextView = view?.findViewById<TextView>(R.id.following_count)
        val followerCountTextView = view?.findViewById<TextView>(R.id.follower_count)

        userProfileRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val bio = document.getString("bio") ?: ""
                    val posts = document.getString("posts") ?: "0"

                    val followers = document.get("followers") as? List<*> ?: emptyList<Any>()
                    val following = document.get("following") as? List<*> ?: emptyList<Any>()

                    val followerCount = followers.size.toString()
                    val followingCount = following.size.toString()


                    if (usernameTextView != null) {
                        usernameTextView.text = otherUsername
                    }
                    if (displayNameTextView != null) {
                        displayNameTextView.text = otherDisplayName
                    }
                    if (bioTextView != null) {
                        bioTextView.text = bio
                    }
                    if (postCount != null) {
                        postCount.text = posts
                    }
                    if (followerCountTextView != null) {
                        followerCountTextView.text = followerCount
                    }
                    if (followingCountTextView != null) {
                        followingCountTextView.text = followingCount
                    }
                }
            }
    }

    private fun loadUserTweets(userId: String) {
        db.collection("text_post")
            .whereEqualTo("uid", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    posts.clear()
                    for (document in snapshot.documents) {
                        val tweet = document.toObject(Post::class.java)
                        if (tweet != null) {
                            tweet.displayName = otherDisplayName.toString()
                            tweet.username = otherUsername.toString()
                            posts.add(tweet)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    private fun checkFollowStatus(otherUserId: String) {
        //val currentUserRef = db.collection("user_profile_info").document(currentUserId)
        currentUserRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val following = document.get("following") as? List<String> ?: emptyList()
                    followBtn.text = if (following.contains(otherUserId)) "Unfollow" else "Follow"
                }
            }
    }
}
