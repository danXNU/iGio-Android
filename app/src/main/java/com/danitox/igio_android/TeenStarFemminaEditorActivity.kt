package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import java.util.*

class TeenStarFemminaEditorActivity: AppCompatActivity() {

    var item: TeenStarFemmina? = null
    var date: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        date.time = intent.getLongExtra("date", -1)
        val itemID = intent.getStringExtra("itemID")
        if (itemID != null) {
            val realm = Realm.getDefaultInstance()
            item = realm.where(TeenStarFemmina::class.java).equalTo("id", itemID).findFirst()
        }

    }


}