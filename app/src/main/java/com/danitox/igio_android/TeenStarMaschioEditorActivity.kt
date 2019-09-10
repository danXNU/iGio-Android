package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import io.realm.Realm
import khronos.beginningOfDay
import khronos.endOfDay
import kotlinx.android.synthetic.main.compagnia_activity.*
import java.util.*


class TeenStarMaschioEditorActivity : AppCompatActivity() {

    var currentVolatileTable: TSMVolatile = TSMVolatile()
    var dbEntry: TeenStarMaschio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        val entryID = intent.getIntExtra("entryID", -1)
        if (entryID > 0) {
            val realm = Realm.getDefaultInstance()
            this.dbEntry = realm.where(TeenStarMaschio::class.java).equalTo("id", entryID).findFirst()
        }

        if (this.dbEntry != null) {
            currentVolatileTable.sentimento8 = dbEntry?.sentimentoTable?.sentimento8
            currentVolatileTable.sentimento14 = dbEntry?.sentimentoTable?.sentimento14
            currentVolatileTable.sentimento20 = dbEntry?.sentimentoTable?.sentimento20
            currentVolatileTable.date = dbEntry?.date?.beginningOfDay ?: Date().beginningOfDay
        }

    }



    fun isDateAvailable(date: Date) : Boolean {
        val realm = Realm.getDefaultInstance()
        val dateFrom = date.beginningOfDay
        val dateTo = dateFrom.endOfDay

        val objects = realm.where(TeenStarMaschio::class.java).between("date", dateFrom, dateTo).findAll()
        return objects.size == 0
    }

    fun dateDidChangeAction(date: Date) {
        this.currentVolatileTable.date = date
    }

    fun saveTeenStarTable() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        dbEntry?.date = currentVolatileTable.date
        if (currentVolatileTable.sentimento8 != null) {
            dbEntry?.sentimentoTable?.sentimento8 = currentVolatileTable.sentimento8!!
        }
        if (currentVolatileTable.sentimento14 != null) {
            dbEntry?.sentimentoTable?.sentimento14 = currentVolatileTable.sentimento14!!
        }
        if (currentVolatileTable.sentimento20 != null) {
            dbEntry?.sentimentoTable?.sentimento20 = currentVolatileTable.sentimento20!!
        }
        realm.insertOrUpdate(dbEntry!!)
        realm.commitTransaction()
    }

}