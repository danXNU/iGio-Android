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
import khronos.toString
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_activity.tableView
import kotlinx.android.synthetic.main.teenstar_m_cell.view.*
import kotlinx.android.synthetic.main.tsm_list.*

class TeenStarMaschioListActivity : AppCompatActivity() {

    var model = TeenStarModel(TeenStarModel.TSModelType.maschio)
    var weeks: List<TeenStarWeek> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsm_list)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        tableView.layoutManager = LinearLayoutManager(this)

        this.add_button.setOnClickListener {
            val newIntent = Intent(this, TeenStarMaschioEditorActivity::class.java)
            this.startActivity(newIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchEntries()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0 until this.weeks.size) {
            val week = weeks[i]
            val newSection = Section(ToxHeader("Daaaang - ${week.startOfWeek}"))

            for (x in 0 until week.tables.size) {
                val entry = week.tables[x]

                val newRow = TSMCell(entry)
                newSection.add(newRow)
            }
            adapter.add(newSection)
        }
        tableView.adapter = adapter
    }

    fun fetchEntries() {
        this.weeks = model.getThemAll()
        fillTableView()
    }

    fun remove(table: TeenStarMaschio) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        table.deleteFromRealm()
        realm.commitTransaction()
        this.weeks.first { it.tables.contains(table) }.tables.removeAll { it == table }
    }

}

class TSMCell(val entry: TeenStarMaschio) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.dayLabel.text = "${entry.date.dayOfWeek()} - ${entry.date.toString("dd/MM/yyyy")}"
        val table = entry.sentimentoTable
        if (table != null) {
            viewHolder.itemView.emoji8Label.text = this.getEmojiFrom(table.sentimento8)
            viewHolder.itemView.emoji14Label.text = this.getEmojiFrom(table.sentimento14)
            viewHolder.itemView.emoji20Label.text = this.getEmojiFrom(table.sentimento20)
        }
    }


    override fun getLayout(): Int {
        return R.layout.teenstar_m_cell
    }

    private fun getEmojiFrom(emozione: Emozione): String {
        return when(emozione) {
            Emozione.fiducioso -> "\uD83D\uDE0C"
            Emozione.aggressività -> "\uD83D\uDE21"
            Emozione.paura -> "\uD83D\uDE31"
            Emozione.tristezza -> "\uD83D\uDE22"
            Emozione.gioia -> "\uD83D\uDE01"
            Emozione.equilibrio -> "\uD83D\uDE10"
            else -> ""
        }

    }

}