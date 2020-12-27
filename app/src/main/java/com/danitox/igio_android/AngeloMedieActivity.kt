package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.MenuItem
import kotlinx.android.synthetic.main.angelo_medie_view.*

class AngeloMedieActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.angelo_medie_view)

        val file = this.assets.open("angelo_medie.html")
        val data = String(file.readBytes())

        val text = Html.fromHtml(data).toString()

        this.textView3.text = text
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}