package com.example.cap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var btn_signup: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var loginText: TextView
    private lateinit var userID: String
    private lateinit var db: FirebaseFirestore
    private lateinit var usersRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        usersRef = db.collection("users")


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        username = findViewById(R.id.username)
        btn_signup = findViewById(R.id.btn_signup)
        loginText = findViewById(R.id.loginText)

        btn_signup.setOnClickListener {
            val username  = username.text.toString()
            val email  = email.text.toString()
            val password = password.text.toString()
            signUp(email,password,username)
        }

        loginText.setOnClickListener {
            val intent = Intent(this, Login ::class.java)
            startActivity(intent)}
    }
    private fun signUp(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if username exists first
        checkIfUsernameExists(username, this) { exists ->
            if (exists) {
                // Username already taken
                Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with user creation if username is available
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()
                                    Log.d("Auth", "Verification email sent.")

                                    // Start checking email verification status
                                    checkEmailVerification(user.uid, email, username)

                                } else {
                                    Log.e("Auth", "Failed to send verification email.", emailTask.exception)
                                }
                            }
                        } else {
                            Toast.makeText(this, "Sign-up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun checkIfUsernameExists(username: String, context: Context, callback: (Boolean) -> Unit){
        usersRef.whereEqualTo("username", username).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Username is taken
                    callback(true)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking username", e)
                Toast.makeText(context, "Error checking username", Toast.LENGTH_SHORT).show()
                callback(false)
            }
}

    private fun checkEmailVerification(userID: String, email: String, username: String) {
        val user = auth.currentUser
        val handler = android.os.Handler()
        val delayMillis: Long = 3000 // Check every 3 seconds

        val verificationChecker = object : Runnable {
            override fun run() {
                user?.reload()?.addOnSuccessListener {
                    if (user.isEmailVerified) {
                        Toast.makeText(this@SignUp, "Email verified!", Toast.LENGTH_SHORT).show()
                        addUserToFirestore(userID, email, username)
                    } else {
                        handler.postDelayed(this, delayMillis)
                    }
                }
            }
        }

        handler.postDelayed(verificationChecker, delayMillis)
    }

    private fun addUserToFirestore(userID: String, email: String, username: String) {
        val user = User(userID, email, username)
        val userinfoRef = db.collection("user_profile_info")
        val userinfo = hashMapOf(
            "username" to username,
            "uid" to userID
        )

        usersRef.document(userID).set(user)
            .addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    Log.d("Firestore", "User successfully added to Firestore!")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.e("Firestore", "Error writing user document", firestoreTask.exception)
                }
            }
            userinfoRef.document(userID).set(userinfo)

            }



}

