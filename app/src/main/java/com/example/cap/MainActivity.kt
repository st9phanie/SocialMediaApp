package com.example.cap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cap.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.BottomNav) as BottomNavigationView
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
    fun setToolbarLayout(layoutRes: Int) {
            val toolbarContainer = findViewById<FrameLayout>(R.id.topbar)
            val inflater = LayoutInflater.from(this)
            toolbarContainer.removeAllViews()
            val newToolbar = inflater.inflate(layoutRes, toolbarContainer, true)

    }
}
