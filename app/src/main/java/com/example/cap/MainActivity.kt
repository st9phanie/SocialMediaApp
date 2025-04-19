package com.example.cap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cap.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        bottomNav = findViewById(R.id.BottomNav) as BottomNavigationView


            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.ic_house -> replaceFragment(HomeFragment())
                    R.id.ic_profile -> replaceFragment(ProfileFragment())
                    R.id.ic_create -> replaceFragment(CreateFragment())
                    R.id.ic_search -> replaceFragment(SearchFragment())
                }
                true
            }
            bottomNav.selectedItemId = R.id.ic_house

    }

    private fun replaceFragment(fragment: Fragment) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfile(userId)}
        val bundle = Bundle()
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        bundle.putString("username", document.getString("username"))
                    }
                    bundle.putString("uid", userId)

                    fragment.arguments = bundle
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container,fragment)
                    transaction.commitAllowingStateLoss()}
        }
    }

    var authStateListener: AuthStateListener =
        AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                val intent = Intent(
                    this@MainActivity,
                    Login::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
        }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }
    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }
    private fun loadUserProfile(userId: String) {
        db.collection("user_profile_info").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val displayName = document.getString("display_name") ?: ""
                    val username = document.getString("username") ?: ""

                    saveToPreferences(username, displayName)
                }
            }
    }

    private fun saveToPreferences(username: String, displayName: String) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("username", username)
            putString("displayName", displayName)
            apply()
        }
    }


}
