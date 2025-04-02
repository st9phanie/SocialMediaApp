package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.view.Window
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
            when(it.itemId) {
                R.id.ic_house -> replaceFragment(HomeFragment())
                R.id.ic_profile -> replaceFragment(ProfileFragment())
                R.id.ic_create -> replaceFragment(CreateFragment())
                R.id.ic_search -> replaceFragment(SearchFragment()) }
            true
        }
        bottomNav.selectedItemId = R.id.ic_house

    }

    private fun replaceFragment(fragment: Fragment) {
        val userId = auth.currentUser?.uid
        val bundle = Bundle()
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        bundle.putString("username", document.getString("username"))
                    }}
        }
        bundle.putString("UID", userId)

        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    var authStateListener: AuthStateListener =
        AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val intent = Intent(
                    this@MainActivity,
                    Login::class.java
                )
                startActivity(intent)
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



}
