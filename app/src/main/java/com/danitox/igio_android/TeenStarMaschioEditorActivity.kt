package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import khronos.beginningOfDay
import khronos.endOfDay
import kotlinx.android.synthetic.main.basic_row.view.*
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_activity.tableView
import kotlinx.android.synthetic.main.tsm_edit_row.view.*
import kotlinx.android.synthetic.main.tsm_list.*
import java.util.*

enum class Orario(val rawValue: String) {
    h8("8:00"),
    h14("14:00"),
    h20("20:00");
}

class TeenStarMaschioEditorActivity : AppCompatActivity() {

    var currentVolatileTable: TSMVolatile = TSMVolatile()
    var dbEntry: TeenStarMaschio? = null

    private val emojiClicked : (Emozione, Orario) -> Unit = { emozione, orario ->
        when(orario) {
            Orario.h8 -> currentVolatileTable.setEmozione(emozione,1)
            Orario.h14 -> currentVolatileTable.setEmozione(emozione,2)
            Orario.h20 -> currentVolatileTable.setEmozione(emozione,3)
        }
    }

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

        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()
        val dateSection = Section(ToxHeader(currentVolatileTable.date.toString()))
        val changeDateRow = BasicRow("Modifica la data")
        dateSection.add(changeDateRow)
        adapter.add(dateSection)

        for (orario in Orario.values()) {
            val newSection = Section(ToxHeader("Sentimento prevalente alle ore ${orario.rawValue}"))
            val editingRow = TSMEditRow(orario, emojiClicked)
            newSection.add(editingRow)
            adapter.add(newSection)
        }

        tableView.adapter = adapter
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
        if (!isDateAvailable(currentVolatileTable.date.beginningOfDay)) {
            return
        }
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

class BasicRow(val text: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mainLabel.text = text
    }

    override fun getLayout(): Int {
        return R.layout.basic_row
    }

}

class TSMEditRow(val orario: Orario, val emozioneClicked: (Emozione, Orario) -> Unit): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.fiduciaButton.setOnClickListener { emozioneClicked.invoke(Emozione.fiducioso, orario) }
        viewHolder.itemView.rabbiaButton.setOnClickListener { emozioneClicked.invoke(Emozione.aggressività, orario) }
        viewHolder.itemView.pauraButton.setOnClickListener { emozioneClicked.invoke(Emozione.paura, orario) }
        viewHolder.itemView.tristezzaButton.setOnClickListener { emozioneClicked.invoke(Emozione.tristezza, orario) }
        viewHolder.itemView.gioiaButton.setOnClickListener { emozioneClicked.invoke(Emozione.gioia, orario) }
        viewHolder.itemView.equilibrioButton.setOnClickListener { emozioneClicked.invoke(Emozione.equilibrio, orario) }
    }

    override fun getLayout(): Int {
        return R.layout.tsm_edit_row
    }
}