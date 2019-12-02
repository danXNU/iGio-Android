package com.danitox.igio_android

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.onesignal.OneSignal
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import khronos.beginningOfDay
import khronos.endOfDay
import java.util.*
//import jdk.nashorn.internal.objects.NativeDate.getTime



class ApplicationSubclass : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val configuration = RealmConfiguration.Builder().schemaVersion(1).migration(MyMigration()).build()
        Realm.setDefaultConfiguration(configuration)

        Log.d("Realm Path", Realm.getDefaultInstance().path)

        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
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
    cal.timeZone = TimeZone.getDefault()
    cal.time = this
    //cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK))
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return cal.time.beginningOfDay
}

fun Date.endOfWeek() : Date {
    val cal = Calendar.getInstance()
    cal.time = this
    //cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK))
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
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

fun manipulateColor(color: Int, factor: Float): Int {
    val a = Color.alpha(color)
    val r = Math.round(Color.red(color) * factor)
    val g = Math.round(Color.green(color) * factor)
    val b = Math.round(Color.blue(color) * factor)
    return Color.argb(
        a,
        Math.min(r, 255),
        Math.min(g, 255),
        Math.min(b, 255)
    )
}

fun getWeeksBetween(a: Date, b: Date): Int {
    var a = a
    var b = b

    if (b.before(a)) {
        return -getWeeksBetween(b, a)
    }
    a = resetTime(a)
    b = resetTime(b)

    val cal = GregorianCalendar(Locale.ITALY)
    cal.time = a
    var weeks = 0
    while (cal.time.before(b)) {
        // add another week
        cal.add(Calendar.WEEK_OF_YEAR, 1)
        weeks++
    }
    return weeks //- 1
}

fun resetTime(d: Date): Date {
    val cal = GregorianCalendar(Locale.ITALY)
    cal.time = d
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}


class SavedValues {
    fun isFirstLaunch(context: Context): Boolean {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        return preferences.getBoolean("isFirstLaunch", true)
    }

    fun setIsFirstLaunch(value: Boolean, context: Context) {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putBoolean("isFirstLaunch", value)
        editor.apply()
    }
}