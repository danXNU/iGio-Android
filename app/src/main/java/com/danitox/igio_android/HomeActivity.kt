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

        this.diocesiButton.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("locType", LocationType.diocesi.value)
            this.startActivity(intent)
        }

        this.citiesButton.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("locType", LocationType.city.value)
            this.startActivity(intent)
        }

        this.resourcesButton.setOnClickListener {
            val intent = Intent(this, SitiActivity::class.java)
            this.startActivity(intent)
        }

        this.verificaButton.setOnClickListener {
            val intent = Intent(this, CompagniaActivity::class.java)
            intent.putExtra("type", UserManager().currentUser().ageScuola.value)
            this.startActivity(intent)
        }

        this.tsm_button.setOnClickListener {
            val intent = Intent(this, TeenStarMaschioListActivity::class.java)
            this.startActivity(intent)
        }

        this.teenStarFButton.setOnClickListener {
            val intent = Intent(this, TeenStarFemminaListActivity::class.java)
            this.startActivity(intent)
        }

        this.gioProButton.setOnClickListener {
            val intent = Intent(this, GioProListActivity::class.java)
            this.startActivity(intent)
        }

        this.regolaButton.setOnClickListener {
            val model = RegolaFetcherModel(this)
            model.createIfNotPresent()

            val intent = Intent(this, RegolaCategorieActivity::class.java)
            intent.putExtra("type", UserManager().currentUser().ageScuola.value)
            this.startActivity(intent)
        }
    }
}