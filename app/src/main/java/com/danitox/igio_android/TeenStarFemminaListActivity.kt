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


class TeenStarFemminaListActivity : AppCompatActivity() {


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