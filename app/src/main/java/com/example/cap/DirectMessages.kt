package com.example.cap

import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DirectMessages : AppCompatActivity() {
    private lateinit var startChat: ImageButton
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.direct_messages)
        backBtn = findViewById(R.id.backBtn)
        startChat = findViewById(R.id.startChat)


        backBtn.setOnClickListener {
            finish()
        }
    }
}