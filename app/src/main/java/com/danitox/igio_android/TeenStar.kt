package com.danitox.igio_android

import android.graphics.Color
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import khronos.beginningOfDay
import khronos.beginningOfMonth
import khronos.endOfDay
import khronos.with
import java.util.*

interface TeenStarDerivative {
    var id: String
    var date: Date
}

open class TeenStarMaschio: RealmObject(), TeenStarDerivative {
    override var id: String = UUID.randomUUID().toString()
    override var date = Date().beginningOfDay
    var sentimentoTable: SentimentoTable? = null
}

open class TeenStarFemmina: RealmObject(), TeenStarDerivative {
    override var id: String = UUID.randomUUID().toString()
    override var date = Date().beginningOfDay
    var cicloTable: CicloTable? = null
}

open class SentimentoTable: RealmObject() {
    private var _sentimento8: Int = Emozione.none.rawValue
    private var _sentimento14: Int = Emozione.none.rawValue
    private var _sentimento20: Int = Emozione.none.rawValue

    var sentimento8: Emozione
        get() { return Emozione.none.getFrom(_sentimento8) }
        set(value) { _sentimento8 = value.rawValue }

    var sentimento14: Emozione
        get() { return Emozione.none.getFrom(_sentimento14) }
        set(value) { _sentimento14 = value.rawValue }

    var sentimento20: Emozione
        get() { return Emozione.none.getFrom(_sentimento20) }
        set(value) { _sentimento20 = value.rawValue }
}

enum class Emozione(val rawValue: Int) {
    none(-1),
    fiducioso(0),
    aggressività(1),
    paura(2),
    tristezza(3),
    gioia(4),
    equilibrio(5);

    fun getFrom(value: Int) : Emozione {
        return when(value) {
            -1 -> none
            0 -> fiducioso
            1 -> aggressività
            2 -> paura
            3 -> tristezza
            4 -> gioia
            5 -> equilibrio
            else -> none
        }
    }

    fun getEmozioneFrom(str: String) : Emozione {
        return when(str) {
            "fiducia" -> fiducioso
            "collera" -> aggressività
            "paura" -> paura
            "tristezza" -> tristezza
            "gioia" ->  gioia
            "equilibrio" -> equilibrio
            else -> none
        }
    }
}

open class CicloTable: RealmObject() {
    private var _cicloColorSelected: Int = CicloColor.none.rawValue

    var cicloColor: CicloColor
        get() { return CicloColor.none.getFrom(_cicloColorSelected) }
        set(value) { _cicloColorSelected = value.rawValue }
}

enum class CicloColor(val rawValue: Int) {
    none(-1),
    rosso(0),
    verde(1),
    giallo(2),
    bianco(3),
    croce(4);

    fun getFrom(value: Int) : CicloColor {
        return when(value) {
            0 -> rosso
            1 -> verde
            2 -> giallo
            3 -> bianco
            4 -> croce
            else -> none
        }
    }

    fun getColorFrom(str: String): CicloColor {
        return when(str) {
            "rosso" -> rosso
            "verde" -> verde
            "giallo" -> giallo
            "bianco" -> bianco
            "croce" -> croce
            else -> none
        }
    }

    fun getViewColor() : Int {
        return when(this) {
            none -> Color.TRANSPARENT
            rosso -> Color.RED
            verde -> Color.GREEN
            giallo -> Color.YELLOW
            bianco -> Color.WHITE
            croce -> Color.CYAN
        }
    }

    fun getDescriptionText() : String {
        return when(this) {
            none -> ""
            rosso -> "Presenza di sangue nei genitali sia che provenga dalla mestruazione o da perdite intermestruali"
            verde -> "Giorni senza muco o con sensazione di secchezza"
            giallo -> "Giorni con presenza di muco appiccicoso e opaco, o una mucosità spessa senza variazioni"
            bianco -> "Giorni di muco cervicale trasparente, elastico, vulva bagnata o sensazione di umidità"
            croce -> "Giorno in cui è avvenuta la mestruazione"
        }
    }
}

class TeenStarWeek(val startOfWeek: Date) {
    var tables: MutableList<TeenStarMaschio> = mutableListOf()
}

class TeenStarModel(val type: TSModelType) {
    enum class TSModelType {
        maschio,
        femmina
    }

    var errorHandler: ((String) -> Unit)? = null

    fun isTodayEmpty(): Boolean {
        val realm = Realm.getDefaultInstance()

        val dateFrom = Date().beginningOfDay
        val dateTo = dateFrom.endOfDay

        if (type == TSModelType.maschio) {
            val count =  realm.where(TeenStarMaschio::class.java).between("date", dateFrom, dateTo).count().toInt()
            return count == 0
        } else {
            val count =  realm.where(TeenStarFemmina::class.java).between("date", dateFrom, dateTo).count().toInt()
            return count == 0
        }
    }

    fun getThemAll() : List<TeenStarWeek> {
        val realm = Realm.getDefaultInstance()
        val allDates = realm.where(TeenStarMaschio::class.java).findAll().map { it.date }

        var weeks: MutableSet<TeenStarWeek> = mutableSetOf()

        for (date in allDates) {
            val newWeek = TeenStarWeek(date.startOfWeek())
            weeks.add(newWeek)
        }

        for (week in weeks) {
            val dateFrom = week.startOfWeek.beginningOfDay
            val dateTo = dateFrom.endOfWeek()

            week.tables = realm.where(TeenStarMaschio::class.java).between("date", dateFrom, dateTo).findAll().sort("date", Sort.DESCENDING)
        }

        return weeks.sortedByDescending { it.startOfWeek }
    }

}