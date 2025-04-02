package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var tweetEditText: EditText
    private lateinit var charCountTextView: TextView
    private lateinit var postButton: Button
    private val maxTweetLength = 280
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tweetEditText = view.findViewById(R.id.tweetEditText)
        charCountTextView = view.findViewById(R.id.charCountTextView)
        postButton = view.findViewById(R.id.postButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tweetEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount / $maxTweetLength"
                postButton.isEnabled = charCount in 1..maxTweetLength
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        postButton.setOnClickListener {
            postTweet()
        }

    }

    private fun postTweet() {
        val tweetContent = tweetEditText.text.toString().trim()
        val userId = auth.currentUser?.uid
        val postRef = db.collection("text_post").document() // Generates a unique ID
        val postID = postRef.id

        if (tweetContent.isEmpty() || userId == null) {
            Toast.makeText(requireContext(), "Tweet cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val tweetData = hashMapOf(
            "uid" to userId,
            "content" to tweetContent,
            "timestamp" to System.currentTimeMillis(),
            "postID" to postID
        )

        db.collection("text_post").add(tweetData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Successfully posted!", Toast.LENGTH_SHORT).show()
                tweetEditText.text.clear()
                increasePostCount(userId)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun increasePostCount(userId: String){
        val userDocRef = db.collection("user_profile_info").document(userId)
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentPostCount = document.getLong("posts") ?: 0
                val newPostCount = currentPostCount + 1
                userDocRef.update("posts", newPostCount)

            } else {
                userDocRef.set(hashMapOf("posts" to 0))
            }
        }
    }

    private fun decreasePostCount(userId: String){
        val userDocRef = db.collection("user_profile_info").document(userId)
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentPostCount = document.getLong("posts") ?: 0
                val newPostCount = currentPostCount - 1
                userDocRef.update("posts", newPostCount)

            } else {
                userDocRef.set(hashMapOf("posts" to 0))
            }
        }
    }

}