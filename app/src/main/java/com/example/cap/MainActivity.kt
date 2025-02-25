package com.example.cap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.cap.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.main)
        swipeRefreshLayout.setOnRefreshListener(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val posts = listOf(
            Post("dn", "Alice", "Hello, world!", "10:00 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM")
        )

        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
    }

    override fun onRefresh() {
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefreshLayout.isRefreshing = false
        },300)
    }
}
