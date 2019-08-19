package com.danitox.igio_android

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

enum class ScuolaType(val value: Int) {
    none(0),
    medie(1),
    biennio(2),
    triennio(3);

    var stringValue: String = ""
        get() {
            return when(this) {
                medie -> "Medie"
                biennio -> "Biennio Superiori"
                triennio -> "Triennio Superiori"
                else -> ""
            }
        }

    fun getFrom(value: Int) : ScuolaType {
        val categoria =  when(value) {
            1 -> medie
            2 -> biennio
            3 -> triennio
            else -> none
        }
        if (categoria == none) { Log.e("Enum error", "ScuolaTyoe ricevuto == .none") }
        return categoria
    }
}

enum class UserGender(val value: Int) {
    none(-1),
    boy(0),
    girl(1);

    fun getFrom(value: Int) : UserGender {
        return when (value) {
            -1 -> none
            0 -> boy
            1 -> girl
            else -> none
        }
    }

    fun getFromString(str: String): UserGender {
        if (str == "Maschio") { return boy }
        else { return girl }
    }
}

open class User: RealmObject() {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var _genderValue : Int = 0
    var _scuolaType : Int = 1
    var apnsToken: String = ""

    var gender: UserGender
        get() { return UserGender.none.getFrom(_genderValue) }
        set(value) { _genderValue = value.value}

    var ageScuola: ScuolaType
        get() { return ScuolaType.none.getFrom(_scuolaType) }
        set(value) { _scuolaType = value.value }
}

class UserManager {
    fun currentUser(): User {
        val realm = Realm.getDefaultInstance()
        val savedUser = realm.where(User::class.java).findFirst()
        if (savedUser == null) {
            realm.beginTransaction()
            val newUser = User()
            realm.insertOrUpdate(newUser)
            realm.commitTransaction()
            return newUser
        } else {
            return savedUser
        }
    }
}