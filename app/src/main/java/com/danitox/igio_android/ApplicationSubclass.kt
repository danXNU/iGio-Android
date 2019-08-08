package com.danitox.igio_android

import android.app.Application
import io.realm.Realm

class ApplicationSubclass : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}