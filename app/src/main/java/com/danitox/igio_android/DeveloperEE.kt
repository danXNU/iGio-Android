package com.danitox.igio_android

import android.content.Context

class DeveloperEE {

    fun shouldDisplayDeveloperName(context: Context): Boolean {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        return preferences.getBoolean("shouldDisplayDeveloperName", false)
    }

    fun setShouldDisplayDeveloperName(value: Boolean, context: Context) {
        val preferences = context.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putBoolean("shouldDisplayDeveloperName", value)
        editor.apply()
    }

}