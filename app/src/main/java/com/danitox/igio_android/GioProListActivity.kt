package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import khronos.toString
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.gio_list_cell.view.*
import kotlinx.android.synthetic.main.tsm_list.*
import kotlinx.android.synthetic.main.tsm_list.tableView
import org.joda.time.LocalDate
import org.joda.time.Weeks

class GioProListActivity : AppCompatActivity() {

    var model = GioProNetAgent()
    var weeks: List<GioProNetWeek> = listOf()

    var selectedEntryID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.tsm_list)

        msgLabel.text = "Nessun GioProNet. Creane uno premendo sul pulsante + "

        this.add_button.setOnClickListener {
            val newIntent = Intent(this, GioProEditorActivity::class.java)
            this.startActivity(newIntent)
        }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)
    }

    override fun onResume() {
        super.onResume()
        fetchEntries()

    }

    fun fetchEntries() {
        this.weeks = model.getThemAll()
        fillTableView()
    }

    fun updateMessage() {
        if (weeks.isEmpty() || weeks.size == 1 && weeks.firstOrNull()?.tables.isNullOrEmpty()) {
            msgLabel.visibility = VISIBLE
        } else {
            msgLabel.visibility = INVISIBLE
        }
    }

    fun fillTableView() {
        updateMessage()

        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0 until this.weeks.size) {
            val week = weeks[i]


            val now = LocalDate()
            val fromDate = LocalDate(week.startOfWeek)

            val weeksCount = Weeks.weeksBetween(fromDate, now).weeks

            var title: String = ""
            if (weeksCount == 0) {
                title = "Questa settimana"
            } else if (weeksCount == 1) {
                title = "1 settimana fa"
            } else {
                title = "$weeksCount settimane fa"
            }

            val newSection = Section(ToxHeader(title))

            for (x in 0 until week.tables.size) {
                val entry = week.tables[x]

                val newRow = GPNListCell(entry, menuItemCallback = {
                    selectedEntryID = it
                }) {
                    val intent = Intent(this, GioProEditorActivity::class.java)
                    intent.putExtra("itemID", it.id)
                    this.startActivity(intent)
                }
                newSection.add(newRow)
            }
            adapter.add(newSection)
        }

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }

    fun remove(item: GioProNet) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        item.deleteFromRealm()
        realm.commitTransaction()
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 121) {
            val realm = Realm.getDefaultInstance()
            val entry = realm.where(GioProNet::class.java).equalTo("id", selectedEntryID).findFirst()
            if (entry != null) {
                remove(entry)
                fetchEntries()
                //fillTableView()
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

class GPNListCell(val item: GioProNet, val menuItemCallback: (String) -> Unit, val clickAction: (GioProNet) -> Unit): Item<ViewHolder>(), View.OnCreateContextMenuListener {

    private lateinit var orariLabel: HashMap<GioProTime, TextView>


    override fun bind(viewHolder: ViewHolder, position: Int) {
        orariLabel = hashMapOf(
            Pair(GioProTime.otto, viewHolder.itemView.emoji8),
            Pair(GioProTime.tredici, viewHolder.itemView.emoji13),
            Pair(GioProTime.diciotto, viewHolder.itemView.emoji18),
            Pair(GioProTime.ventiquattro, viewHolder.itemView.emoji24)
        )

        viewHolder.itemView.setOnCreateContextMenuListener(this)
        viewHolder.itemView.dateLabel.text = item.date.toString("dd/MM/yyyy")

        for ((orario, label) in orariLabel) {
            val task = item.getTask(orario).taskType
            if (task.emoji() == null) {
                label.setBackgroundResource(task.imageName()!!)
                label.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
            } else {
                label.text = task.emoji()
            }
        }

        viewHolder.itemView.setOnClickListener {
            clickAction.invoke(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 121, 0, "Rimuovi")
        menuItemCallback.invoke(item.id)
    }

    override fun getLayout(): Int {
        return R.layout.gio_list_cell
    }
}