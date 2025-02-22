package com.example.cap

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View
import com.google.firebase.firestore.firestore

class SignUp : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var btn_signup: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var loginText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

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
            signUp(email,password)
        }

        loginText.setOnClickListener {
            val intent = Intent(this, Login ::class.java)
            startActivity(intent)}
    }
    private fun signUp(email:String, password:String){
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@SignUp,MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this@SignUp,"Some error occurred",Toast.LENGTH_SHORT).show()
                    }
                }
        }}

}

