package com.example.cap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is logged in
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User not logged in
            startActivity(Intent(this, Login::class.java))
        }

        finish()
    }
}
