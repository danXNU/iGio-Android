package com.danitox.igio_android

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.teenstar_m_cell.view.*
import kotlinx.android.synthetic.main.teenstar_m_cell.view.dayLabel
import kotlinx.android.synthetic.main.tsf_list_activity.*
import kotlinx.android.synthetic.main.tsf_list_activity.tableView
import kotlinx.android.synthetic.main.tsf_list_item.view.*
import java.util.*


class TeenStarFemminaListActivity : AppCompatActivity() {

    var selectedMonth: Int = 0
        set(value) {
            field = value
            if (selectedMonth <= 0) { return }
            if (selectedYear <= 0) { return }
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.set(Calendar.MONTH, selectedMonth)
            currentDate = cal.time
        }

    var selectedYear: Int = 0
        set(value) {
            field = value
            if (selectedYear <= 0) { return }
            if (selectedMonth <= 0) { return }
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.set(Calendar.YEAR, selectedYear)
            currentDate = cal.time
        }

    var currentDate: Date = Date()
        set(value) {
            field = value
            updateDates()
            fetchItems(currentDate)
        }

    var allItems: List<TeenStarFemmina> = listOf()
    var allDates: List<Date> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsf_list_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider2 = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        tableView.addItemDecoration(divider)
        tableView.addItemDecoration(divider2)
    }

    override fun onResume() {
        super.onResume()
        fillTableview()
    }

    fun fillTableview() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.spanCount = 4

        for (i in 0 .. 30) {
            val newItem = TSFListItem()
            adapter.add(newItem)
        }


        tableView.layoutManager = GridLayoutManager(this, adapter.spanCount).apply { spanSizeLookup = adapter.spanSizeLookup }
        tableView.adapter = adapter
    }

    fun updateDates() {

    }

    fun fetchItems(date: Date) {

    }

}

class TSFListItem: Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.dayLabel.text = "ASD"
        viewHolder.itemView.cicloColorView.setBackgroundColor(Color.RED)
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 4
    }

    override fun getLayout(): Int {
        return R.layout.tsf_list_item
    }
}