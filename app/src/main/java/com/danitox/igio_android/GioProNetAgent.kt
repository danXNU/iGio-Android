package com.danitox.igio_android

import io.realm.Realm
import io.realm.Sort
import khronos.beginningOfDay
import khronos.endOfDay
import java.util.*

class GioProNetAgent {
    var errorHandler: ((String) -> Unit)? = null

    fun isDateAvailable(item: GioProNet): Boolean {
        val realm = Realm.getDefaultInstance()
        val dateFrom = item.date.beginningOfDay
        val dateTo = dateFrom.endOfDay

        val objects = realm.where(GioProNet::class.java).between("date", dateFrom, dateTo).findAll()
        if (objects.contains(item)) {
            return true
        } else {
            return  objects.size == 0
        }
    }

    fun isDateAvailable(date: Date): Boolean {
        val realm = Realm.getDefaultInstance()
        val dateFrom = date
        val dateTo = dateFrom.endOfDay

        val objects = realm.where(GioProNet::class.java).between("date", dateFrom, dateTo).findAll()
        return objects.size == 0
    }

    fun getThemAll(): List<GioProNetWeek> {
        val realm = Realm.getDefaultInstance()
        val allDates = realm.where(GioProNet::class.java).findAll().map { it.date }

        val weeks : MutableList<GioProNetWeek> = mutableListOf()

        for (date in allDates) {
            val existingWeek: GioProNetWeek? = weeks.firstOrNull { it.startOfWeek.beginningOfDay.isCloseToDayEqualTo(date.startOfWeek().beginningOfDay) }

            if (existingWeek == null) {
                val newWeek = GioProNetWeek(date.startOfWeek())
                weeks.add(newWeek)

                val dateFrom = newWeek.startOfWeek.beginningOfDay
                val dateTo = dateFrom.endOfWeek()

                newWeek.tables = realm.where(GioProNet::class.java)
                    .between("date", dateFrom, dateTo)
                    .findAll().sort("date", Sort.DESCENDING)
            }

        }

        return weeks.sortedByDescending { it.startOfWeek }
    }


    fun fetchEntries() : List<GioProNet> {
        val realm = Realm.getDefaultInstance()
        return realm.where(GioProNet::class.java).sort("date", Sort.DESCENDING).findAll()
    }

    fun getNewEntry(): GioProNet {
        return GioProNet()
    }

}