package com.danitox.igio_android

//iOS style DispatchGroup
class DispatchGroup {
    private var count = 0
    private var runnable: (() -> Unit)? = null

    @Synchronized
    fun enter() {
        count++
    }

    @Synchronized
    fun leave() {
        count--
        notifyGroup()
    }

    fun notify(r: () -> Unit) {
        runnable = r
        notifyGroup()
    }

    private fun notifyGroup() {
        if (count <= 0) {
            runnable?.invoke()
        }
    }
}