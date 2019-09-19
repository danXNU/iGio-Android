package com.danitox.igio_android

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.tsf_editor_item.view.*
import kotlinx.android.synthetic.main.tsf_list_activity.*
import java.util.*

class TeenStarFemminaEditorActivity: AppCompatActivity() {

    lateinit var item: TeenStarFemmina
    var date: Date? = null
    var currentVolatileTable: TSFVolatile = TSFVolatile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.compagnia_activity)

        val itemID = intent.getStringExtra("itemID")
        if (itemID == null) {
            date = Date().apply { time = intent.getLongExtra("date", -1) }

            item = TeenStarFemmina()
            item.date = date!!
        } else {
            val realm = Realm.getDefaultInstance()
            item = realm.where(TeenStarFemmina::class.java).equalTo("id", itemID).findFirst()!!
            currentVolatileTable.ciclo = item.cicloTable?.cicloColor
        }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        currentVolatileTable.date = item.date
    }

    override fun onResume() {
        super.onResume()
        fillTableview()
    }

    fun fillTableview() {
        val adapter = GroupAdapter<ViewHolder>()

        for (color in CicloColor.values()) {
            if (color == CicloColor.none) { continue }
            val newItem = TSFEditorItem(color, item.cicloTable?.cicloColor == color)
            newItem.clickAction = {

            }
            adapter.add(newItem)
        }

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }


}

class TSFEditorItem(private val cicloColor: CicloColor, private val isSelected: Boolean) : Item<ViewHolder>() {

    var clickAction: ((CicloColor) -> Unit)? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.cicloColorView.setBackgroundColor(cicloColor.getViewColor())
        viewHolder.itemView.cicloDescriptionLabel.text = cicloColor.getDescriptionText()

        if (isSelected) {
            viewHolder.itemView.setBackgroundColor(Color.GREEN)
        }

        viewHolder.itemView.setOnClickListener { clickAction?.invoke(cicloColor) }
    }

    override fun getLayout(): Int {
        return R.layout.tsf_editor_item
    }
}