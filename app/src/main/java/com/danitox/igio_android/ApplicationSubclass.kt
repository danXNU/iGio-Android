package com.danitox.igio_android

import android.app.Application
import android.util.Log
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import khronos.beginningOfDay
import khronos.endOfDay
import java.util.*

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

fun Date.startOfWeek() : Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK))
    return cal.time.beginningOfDay
}

fun Date.endOfWeek() : Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK))
    return cal.time.endOfDay
}

fun Date.dayOfWeek() : String {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
}


fun Date.isCloseToDayEqualTo(date: Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = this

    val cal2 = Calendar.getInstance()
    cal2.time = date
    return cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}