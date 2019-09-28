package com.danitox.igio_android

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import khronos.beginningOfDay
import java.util.*

open class GioProNet: RealmObject() {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var date: Date = Date().beginningOfDay
    var tasks : RealmList<GioProNetTask> = RealmList()

    fun getTask(time: GioProTime) : GioProNetTask {
        val taskObject = this.tasks.firstOrNull { it.time == time }
        if (taskObject != null) {
            return taskObject
        } else {
            val newTask = GioProNetTask()
            newTask.time = time
            newTask.taskType = TaskType.none

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            tasks.add(newTask)
            realm.commitTransaction()

            return newTask
        }
    }


    fun isConsideredEmpty() : Boolean {
        if (this.tasks.isEmpty()) { return true }
        var counter = 0
        for (task in this.tasks) {
            if (task.taskType != TaskType.none) {
                counter += 1
            }
        }
        return counter == 0
    }
}

open class GioProNetTask: RealmObject() {
    @LinkingObjects("tasks") val gioProItem: RealmResults<GioProNet>? = null
    var _taskType: Int = 0
    var _time = 0

    var taskType: TaskType
        get() { return TaskType.none.getFrom(_taskType) }
        set(value) { _taskType = value.rawValue }

    var time: GioProTime
        get() { return GioProTime.none.getFrom(_time) }
        set(value) { _time = value.rawValue }
}

enum class TaskType(val rawValue: Int) {
    none(0),
    whatsapp(1),
    gaming(2),
    amici(3),
    libri(4),
    sport(5),
    riposo(6);

    fun imageName(): Int? {
        return when(this) {
            whatsapp -> R.drawable.wa_logo_native
            gaming -> null
            else -> null
        }
    }

    fun emoji(): String? {
        return when(this) {
            none -> "NULL"
            whatsapp -> null
            gaming -> "\uD83C\uDFAE"
            amici -> "\uD83D\uDC6B"
            libri -> "\uD83D\uDCDA"
            sport -> "⚽️"
            riposo -> "\uD83D\uDECC"
        }
    }

    fun stringValue(): String {
        return when(this) {
            none -> "Vuoto"
            whatsapp -> "Chat e Social"
            gaming -> "Web e Gaming"
            amici -> "Amici e Famiglia"
            libri -> "Dovere quotidiano"
            sport -> "Sport e Hobby"
            riposo -> "Riposo"
        }
    }

    fun getFrom(value: Int) : TaskType {
        return when(value) {
            1 -> whatsapp
            2 -> gaming
            3 -> amici
            4 -> libri
            5 -> sport
            6 -> riposo
            else -> none
        }
    }

}

enum class GioProTime(val rawValue: Int) {
    none(0),
    otto(1),
    tredici(2),
    diciotto(3),
    ventiquattro(4);

    fun stringValue() : String {
        return when(this) {
            none -> "NULL"
            otto -> "8:00"
            tredici -> "13:00"
            diciotto -> "18:00"
            ventiquattro -> "24:00"
        }
    }

    fun getFrom(value: Int) : GioProTime {
        return when(value) {
            1 -> otto
            2 -> tredici
            3 -> diciotto
            4 -> ventiquattro
            else -> none
        }
    }
}

class GioProNetWeek(val startOfWeek: Date) {
    var tables: List<GioProNet> = listOf()
}