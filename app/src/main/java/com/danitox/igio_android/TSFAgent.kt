package com.danitox.igio_android

import io.realm.Realm
import io.realm.Sort
import khronos.DateRange
import java.time.LocalDate
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

    fun getArrayDatesFromRange(range: ClosedRange<Date>): List<Date> {
        val datesInRange : MutableList<Date> = mutableListOf()
        val cal = Calendar.getInstance()
        cal.time = range.start

        val endCalendar = Calendar.getInstance()
        endCalendar.time = range.endInclusive

        while (cal.before(endCalendar)) {
            val result = cal.time
            datesInRange.add(result)
            cal.add(Calendar.DATE, 1)
        }
        return datesInRange
    }

}

/*
data class Date (private val year: Int, private val month: Int, private val day: Int)
    : Comparable<Date> {

    companion object {
        const val MONTHS_IN_A_YEAR = 12
    }

    init {
        if (month > MONTHS_IN_A_YEAR || month <  0) {
            throw IllegalStateException("Month must between 1 - $MONTHS_IN_A_YEAR")
        }
        if (day > daysInMonth(month, year)) {
            throw IllegalStateException("Day $day not valid in month $month of year $year")
        }
    }

    override operator fun compareTo(other: Date): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.day > other.day) return 1
        if (this.day < other.day) return -1
        return 0
    }

    operator fun inc(): Date {
        return when {
            (day < daysInMonth(month, year))
            -> Date(year, month, day+1)
            (month < MONTHS_IN_A_YEAR)
            -> Date(year, month + 1, 1)
            else -> Date(year + 1, 1, 1)
        }
    }

    private fun daysInMonth(month: Int, year: Int): Int {
        return when (month) {
            4, 6, 9, 11 -> 30
            2 -> if (leapYear(year)) 29 else 28
            else -> 31
        }
    }

    private fun leapYear(year: Int): Boolean {
        return when {
            year % 400 == 0 -> true
            year % 100 == 0 -> false
            else -> year % 4 == 0
        }
    }

    operator fun rangeTo(that: Date) = DateRange(this, that)
}

class DateRange(override val start: Date, override val endInclusive: Date)
    : ClosedRange<Date>, Iterable<Date> {

    override fun iterator(): Iterator<Date> {
        return DateIterator(start, endInclusive)
    }

}

class DateIterator(start: Date, private val endInclusive: Date) : Iterator<Date> {
    private var current = start

    override fun hasNext(): Boolean {
        return current <= endInclusive
    }

    override fun next(): Date {
        return current++
    }
}*/