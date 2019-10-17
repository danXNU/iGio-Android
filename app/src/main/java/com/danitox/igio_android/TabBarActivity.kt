package com.danitox.igio_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class TabBarActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replcaeFragment(HomeActivity())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_risorse -> {

                replcaeFragment(SocialActivity())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_social -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_bar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        replcaeFragment(HomeActivity())
    }

    fun replcaeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.container, fragment)
        fragmentManager.commit()
    }
}
