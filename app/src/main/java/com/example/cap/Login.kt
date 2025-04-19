package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.sendbird.android.SendBird


class Login : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btn_login: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var forgotPass: TextView
    private lateinit var signUpText: TextView

    private val authStateListener =
        AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val intent = Intent(
                    this@Login,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        enableEdgeToEdge()


        setContentView(R.layout.activity_login)

        SendBird.init("APP_ID_HERE", this)

        auth = FirebaseAuth.getInstance()
        val authStateListener =
            AuthStateListener { firebaseAuth ->
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val intent = Intent(
                        this@Login,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        btn_login = findViewById(R.id.btn_login)
        signUpText = findViewById(R.id.signUpText)
        forgotPass = findViewById(R.id.forgotPass)


        btn_login.setOnClickListener {
            val useremail = email.text.toString()
            val userpassword = password.text.toString()

            if (useremail.isEmpty()) {
                email.error = "Email cannot be empty"
                email.requestFocus()
                return@setOnClickListener }

            if (userpassword.isEmpty()) {
                password.error = "Password cannot be empty"
                password.requestFocus()
                return@setOnClickListener }
            login(useremail,userpassword)
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUp ::class.java)
            startActivity(intent)}
        forgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPassword ::class.java)
            startActivity(intent)}
}

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthInvalidUserException -> "No account found with this email."
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect credentials."
                        else -> exception?.localizedMessage ?: "Login failed. Please try again."
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
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
