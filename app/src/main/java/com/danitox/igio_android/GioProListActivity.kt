package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.tsm_list)

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

    fun fillTableView() {
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

                val newRow = GPNListCell(entry) {
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
}

class GPNListCell(val item: GioProNet, val clickAction: (GioProNet) -> Unit): Item<ViewHolder>() {

    private lateinit var orariLabel: HashMap<GioProTime, TextView>


    override fun bind(viewHolder: ViewHolder, position: Int) {
        orariLabel = hashMapOf(
            Pair(GioProTime.otto, viewHolder.itemView.emoji8),
            Pair(GioProTime.tredici, viewHolder.itemView.emoji13),
            Pair(GioProTime.diciotto, viewHolder.itemView.emoji18),
            Pair(GioProTime.ventiquattro, viewHolder.itemView.emoji24)
        )

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

    override fun getLayout(): Int {
        return R.layout.gio_list_cell
    }
}