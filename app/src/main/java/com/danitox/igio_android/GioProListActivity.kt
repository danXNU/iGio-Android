package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.tsm_list.*

class GioProListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.tsm_list)

        this.add_button.setOnClickListener {
            val newIntent = Intent(this, GioProEditorActivity::class.java)
            this.startActivity(newIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {

    }
}