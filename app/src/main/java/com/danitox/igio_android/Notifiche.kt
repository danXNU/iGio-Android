package com.danitox.igio_android

import android.content.Context
import kotlin.contracts.contract

class Notifiche {

    enum class NotificheType(val rawValue: Int) {
        none(0),
        eventiMGS(1),
        consigliDB(2),
        sacramenti(4),
        angeloCustode(8);

        fun stringValue() : String {
            return when(this) {
                eventiMGS -> "Eventi MGS"
                consigliDB -> "Consigli di Don Bosco"
                sacramenti -> "Promemoria di vita spirituale"
                angeloCustode -> "Missione dell'angelo custode"
                else -> ""
            }
        }

        fun getStringFrom(type: NotificheType) : String {
            return type.stringValue()
        }

        fun tagKey() : String {
            return when(this) {
                eventiMGS -> "eventiMGS"
                consigliDB -> "consigliDB"
                sacramenti -> "sacramenti"
                angeloCustode -> "missioneAC"
                else -> ""
            }
        }
    }

    fun encode(notifiche: Set<NotificheType>) : Int {
        if (notifiche.isEmpty()) { return 0 }
        var base = 0
        for (notif in notifiche) {
            base += notif.rawValue
        }
        
        return base
    }

    
    fun decode(integer: Int) : Set<NotificheType> {
        if (integer <= 0) { return setOf() }
        
        val notifiche: MutableSet<NotificheType> = mutableSetOf()

        for (notifica in NotificheType.values().filter { it != NotificheType.none }) {
            val base = integer
            val notifRawValue = notifica.rawValue

            val result = base and notifRawValue

            if (result > 0) {
                notifiche.add(notifica)
            }
        }

        return notifiche.toSet()
    }

    fun areNotificheActive(context: Context): Boolean {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        return preferences.getBoolean("areNotificheActive", false)
    }

    fun setAreNotificheActive(value: Boolean, context: Context) {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putBoolean("areNotificheActive", value)
        editor.apply()
    }

    fun setNotificaTypeActive(type: NotificheType, active: Boolean, context: Context) {
        val oldSet = this.getActiveNotifiche(context)
        val newSet: MutableSet<NotificheType> = oldSet.toMutableSet()
        if (active) {
            if (!newSet.contains(type)) {
                newSet.add(type)
            }
        } else {
            if (newSet.contains(type)) {
                newSet.remove(type)
            }
        }
        setActiveNotifiche(context, newSet.toSet())
    }

    fun getActiveNotifiche(context: Context) : Set<NotificheType> {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val integer = preferences.getInt("notificheRawValue", 0)
        return this.decode(integer)
    }

    fun setActiveNotifiche(context: Context, notifiche: Set<NotificheType>) {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        val integer = this.encode(notifiche)
        editor.putInt("notificheRawValue", integer)
        editor.apply()
    }


    fun userDismissedNotifications(): Boolean {
        return false
    }

    fun requestAuthorization() {

    }

    fun subscribeToActiveNotifications() {

    }

    fun unsubscribeToAllNotifications() {

    }

    fun openSettings() {

    }

    fun updateStatus() {

    }
}