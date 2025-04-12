package com.example.cap

import ProfileViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : ComponentActivity() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        // Initialize Firestore and ViewModel
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setContent {
            EditProfileScreen(userID = FirebaseAuth.getInstance().currentUser?.uid ?: "")
        }
    }
}
