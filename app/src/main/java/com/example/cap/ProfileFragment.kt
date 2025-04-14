package com.example.cap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: MyPostAdapter
    private lateinit var editProfileBtn: Button
    private lateinit var more: ImageButton
    private lateinit var displayNameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileBtn = view.findViewById(R.id.edit_profile_btn)
        displayNameTextView = view.findViewById(R.id.displayName)
        bioTextView = view.findViewById(R.id.bio)
        usernameTextView = view.findViewById(R.id.username)
        postCount = view.findViewById(R.id.post_count)
        followerCountTextView = view.findViewById(R.id.follower_count)
        followingCountTextView = view.findViewById(R.id.following_count)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = auth.currentUser?.uid

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        usernameTextView.text = sharedPref.getString("username", "")
        displayNameTextView.text = sharedPref.getString("displayName", "")
        bioTextView.text = sharedPref.getString("bio", "")
        postCount.text = sharedPref.getString("posts", "")
        followerCountTextView.text = sharedPref.getString("followerCount", "")
        followingCountTextView.text = sharedPref.getString("followingCount", "")

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = MyPostAdapter(posts) { post ->
            deletePost(post, userId.toString())
        }
        recyclerView.adapter = postAdapter

        userId?.let {
            loadUserProfile(it)
            loadUserPosts(it)
        }

        editProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile(userId: String) {
        db.collection("user_profile_info").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val displayName = document.getString("display_name") ?: ""
                    val bio = document.getString("bio") ?: ""
                    val username = document.getString("username") ?: ""
                    val posts = document.getString("posts") ?: "0"
                    val followers = document.get("followers") as? List<*> ?: emptyList<Any>()
                    val following = document.get("following") as? List<*> ?: emptyList<Any>()

                    displayNameTextView.text = displayName
                    bioTextView.text = bio
                    usernameTextView.text = username
                    postCount.text = posts
                    followerCountTextView.text = followers.size.toString()
                    followingCountTextView.text = following.size.toString()

                    saveToPreferences(username, displayName, bio, posts, followers.size.toString(), following.size.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToPreferences(username: String, displayName: String, bio: String, postCount: String, followerCount: String, followingCount: String) {
        activity?.let {
            val sharedPref = it.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("username", username)
                putString("displayName", displayName)
                putString("bio", bio)
                putString("posts", postCount)
                putString("followingCount", followingCount)
                putString("followerCount", followerCount)
                apply()
            }
        }
    }

    private fun loadUserPosts(userId: String) {

        db.collection("text_post")
            .whereEqualTo("uid", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->

                if (snapshot != null) {
                    posts.clear()
                    for (document in snapshot.documents) {
                        val post = document.toObject(Post::class.java)
                        post?.postID = document.id
                        if (post != null) {
                            post.displayName = displayNameTextView.text.toString()
                            post.username = usernameTextView.text.toString()
                            posts.add(post)
                        }
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun deletePost(post: Post, userId: String) {
        FirebaseFirestore.getInstance().collection("text_post")
            .document(post.postID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                posts.remove(post)
                postAdapter.notifyDataSetChanged()
                postCount.text = posts.size.toString()
                db.collection("user_profile_info").document(userId)
                    .update("posts", posts.size.toString())
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }
}
