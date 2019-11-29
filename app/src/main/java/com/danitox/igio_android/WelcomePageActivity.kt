package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.welcome_page.*

class WelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.welcome_page)

        startButton.setOnClickListener {
            val intent = Intent(this, WelcomeEditActivity::class.java)
            startActivity(intent)
        }
    }


}