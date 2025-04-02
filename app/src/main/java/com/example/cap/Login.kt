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
            val email = email.text.toString()
            val password = password.text.toString()
            login(email,password)}

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUp ::class.java)
            startActivity(intent)}
        forgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPassword ::class.java)
            startActivity(intent)}
}

    private fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login,"User does not exist or wrong credentials", Toast.LENGTH_SHORT).show()
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
