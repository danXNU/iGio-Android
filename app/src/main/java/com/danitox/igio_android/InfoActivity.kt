package com.danitox.igio_android

import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.MenuItem
import com.google.gson.GsonBuilder
import io.realm.Realm
import kotlinx.android.synthetic.main.info_view.*

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_view)

        val stream = assets.open("welcomeText.html")
        val rawBytes = stream.readBytes()
        stream.close()
        val str = String(rawBytes)

        webView.loadData(str, "text/html", "utf-8")
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