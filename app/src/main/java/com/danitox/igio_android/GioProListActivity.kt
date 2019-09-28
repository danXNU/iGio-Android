package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import khronos.toString
import kotlinx.android.synthetic.main.gio_list_cell.view.*
import kotlinx.android.synthetic.main.tsm_list.*
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

                //val newRow = BasicRow("${entry.date.toString("dd/MM/yyyy")}")
                val newRow = GPNListCell(entry)
                newSection.add(newRow)
            }
            adapter.add(newSection)
        }

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }
}

class GPNListCell(val item: GioProNet): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.dateLabel.text = item.date.toString("dd/MM/yyyy")
    }

    override fun getLayout(): Int {
        return R.layout.gio_list_cell
    }
}