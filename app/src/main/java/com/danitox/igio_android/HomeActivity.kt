package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.home_layout.*

class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home_layout)
        this.notesButton.text = "Diario personale"


        this.notesButton.setOnClickListener {
            val intent = Intent(this, NoteListActivity::class.java)
            this.startActivity(intent)
        }

        this.resourcesButton.setOnClickListener {
            val request = ToxNetworkRequest()
            request.requestType = RequestType.localizedSites

            val agent = NetworkAgent<String>()
            agent.executeNetworkRequest(request, String::class.java) { response, error ->
                if (error != null) {
                    Log.e("Network", error)
                }

                if (response != null) {
                    println("Ho ottenuto il response correttamente")
                }

            }
        }
    }
}