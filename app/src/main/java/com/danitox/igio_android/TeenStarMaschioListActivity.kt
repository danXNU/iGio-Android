package com.danitox.igio_android

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
import java.util.*
import kotlin.math.abs

class TeenStarMaschioListActivity : AppCompatActivity() {

    var model = TeenStarModel(TeenStarModel.TSModelType.maschio)
    var weeks: List<TeenStarWeek> = listOf()

    var selectedEntryID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsm_list)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        tableView.layoutManager = LinearLayoutManager(this)

        this.add_button.setOnClickListener {
            val newIntent = Intent(this, TeenStarMaschioEditorActivity::class.java)
            newIntent.putExtra("isNewEntry", true)
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

            val now = Date()
            val cal = Calendar.getInstance()
            cal.time = Date()
            val nowWeeks = cal.get(Calendar.WEEK_OF_YEAR)
            cal.time = week.startOfWeek
            val oldWeeks = cal.get(Calendar.WEEK_OF_YEAR)

            val diffWeeks = abs(oldWeeks - nowWeeks)
            var title: String = ""
            if (diffWeeks == 0) {
                title = "Questa settimana"
            } else if (diffWeeks == 1) {
                title = "1 settimana fa"
            } else {
                title = "$diffWeeks settimane fa"
            }

            val newSection = Section(ToxHeader(title))

            for (x in 0 until week.tables.size) {
                val entry = week.tables[x]

                val newRow = TSMCell(entry, clickAction = {
                    val newIntent = Intent(this, TeenStarMaschioEditorActivity::class.java)
                    newIntent.putExtra("entryID", entry.id)
                    this.startActivity(newIntent)
                }) { entryIDselected ->
                    selectedEntryID = entryIDselected
                }
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
        //this.weeks.firstOrNull { it.tables.contains(table) }.tables.removeAll { it == table }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 121) {
            val realm = Realm.getDefaultInstance()
            val entry = realm.where(TeenStarMaschio::class.java).equalTo("id", selectedEntryID).findFirst()
            if (entry != null) {
                remove(entry)
                fillTableView()
                showRemoveMessage()
            }
            return true
        } else {
            return super.onContextItemSelected(item)
        }

    }

    private fun showRemoveMessage() {
        Snackbar.make(this.tableView, "Item rimosso con successo!", Snackbar.LENGTH_SHORT).show()
    }

}

class TSMCell(val entry: TeenStarMaschio, val clickAction: () -> Unit, val menuItemCallback: (String) -> Unit) : Item<ViewHolder>(), View.OnCreateContextMenuListener {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.dayLabel.text = "${entry.date.dayOfWeek()} - ${entry.date.toString("dd/MM/yyyy")}"
        val table = entry.sentimentoTable
        if (table != null) {
            viewHolder.itemView.emoji8Label.text = this.getEmojiFrom(table.sentimento8)
            viewHolder.itemView.emoji14Label.text = this.getEmojiFrom(table.sentimento14)
            viewHolder.itemView.emoji20Label.text = this.getEmojiFrom(table.sentimento20)
        }
        viewHolder.itemView.setOnClickListener { clickAction.invoke() }
        viewHolder.itemView.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 121, 0, "Rimuovi")
        menuItemCallback.invoke(entry.id)
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