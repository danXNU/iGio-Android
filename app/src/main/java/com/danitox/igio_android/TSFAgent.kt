package com.danitox.igio_android

import io.realm.Realm
import io.realm.Sort
import khronos.DateRange
import java.util.*

class TSFAgent {
    fun getMonthRange(date: Date) : ClosedRange<Date> {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = cal.time

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val finishDate = cal.time

        return startOfMonth .. finishDate
    }

    fun getFarestDate() : Date {
        val realm = Realm.getDefaultInstance()
        val obj = realm.where(TeenStarFemmina::class.java).sort("date", Sort.ASCENDING).findFirst()
        return obj?.date ?: Date()
    }

    fun getYearsList(date: Date) : List<Int> {
        val cal = Calendar.getInstance()
        cal.time = date

        val lowerDate = cal.get(Calendar.YEAR)
        cal.time = Date()
        val thisYear = cal.get(Calendar.YEAR)

        return (lowerDate..thisYear).toList()
    }

    fun getMonthsList(year: Int) : List<Int> {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val thisYear = cal.get(Calendar.YEAR)
        if (year > thisYear) {
            return listOf()
        }

        if (year < thisYear) {
            return (1 .. 12).toList()
        }

        if (year == thisYear) {
            cal.time = Date()
            val thisMonth = cal.get(Calendar.MONTH)
            return (0..thisMonth).toList()
        }
        return listOf()
    }

    fun fetchItems(date: Date) : List<TeenStarFemmina> {
        val range = this.getMonthRange(date)

        val realm = Realm.getDefaultInstance()
        return realm.where(TeenStarFemmina::class.java).between("date", range.start, range.endInclusive).findAll()
    }


    fun getTodayComponents() : Pair<Int, Int> {
        val cal = Calendar.getInstance()
        cal.time = Date()

        return Pair(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    }

}