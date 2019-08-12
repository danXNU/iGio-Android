package com.danitox.igio_android

import android.app.Application
import android.util.Log
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

class ApplicationSubclass : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val configuration = RealmConfiguration.Builder().schemaVersion(1).migration(MyMigration()).build()
        Realm.setDefaultConfiguration(configuration)

        Log.d("Realm Path", Realm.getDefaultInstance().path)
    }
}

class MyMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        var oldVersion: Int = oldVersion.toInt()

        if (oldVersion == 0) {
            schema.create("Location")
                .addPrimaryKey("id")
                .addField("name", String::class.java)
                .addField("isSelected", Boolean::class.java)
                .addField("_type", Int::class.java)


            schema.create("SitoWeb")
                .addPrimaryKey("id")
                .addField("order", Int::class.java)
                .addField("nome", String::class.java)
                .addField("descrizione", String::class.java)
                .addField("urlString", String::class.java)
                .addField("_scuolaType", Int::class.java)
                .addField("categoria", Int::class.java)
                .addRealmObjectField("location", schema.get("Location")!!)

            oldVersion += 1
        }
    }
}