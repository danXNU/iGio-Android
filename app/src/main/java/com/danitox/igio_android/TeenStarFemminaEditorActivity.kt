package com.danitox.igio_android

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.tsf_editor_item.view.*
import kotlinx.android.synthetic.main.tsf_editor_item.view.cicloColorView
import kotlinx.android.synthetic.main.tsf_list_activity.*
import kotlinx.android.synthetic.main.tsf_list_item.view.*
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

    override fun onPause() {
        super.onPause()
        saveTeenStarTable()
    }

    fun fillTableview() {
        val adapter = GroupAdapter<ViewHolder>()

        for (color in CicloColor.values()) {
            if (color == CicloColor.none) { continue }
            val newItem = TSFEditorItem(color, currentVolatileTable.ciclo == color)
            newItem.clickAction = {
                updateCicloSelected(it)
            }
            adapter.add(newItem)
        }

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }

    fun updateCicloSelected(cicloColor: CicloColor) {
        this.currentVolatileTable.ciclo = cicloColor
        fillTableview()
    }

    fun saveTeenStarTable() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        if (currentVolatileTable.ciclo != null) {
            item.cicloTable?.cicloColor = currentVolatileTable.ciclo!!
        }

        realm.insertOrUpdate(item)
        realm.commitTransaction()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

class TSFEditorItem(private val cicloColor: CicloColor, private val isSelected: Boolean) : Item<ViewHolder>() {

    var clickAction: ((CicloColor) -> Unit)? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.cicloColorView.setBackgroundColor(cicloColor.getViewColor())
        viewHolder.itemView.cicloDescriptionLabel.text = cicloColor.getDescriptionText()

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(cicloColor.getViewColor())
        shape.cornerRadius = 19f //20f
        viewHolder.itemView.cicloColorView.background = shape

        if (isSelected) {
            viewHolder.itemView.setBackgroundColor(manipulateColor(Color.GREEN, 0.6f))
        }

        viewHolder.itemView.setOnClickListener { clickAction?.invoke(cicloColor) }
    }

    override fun getLayout(): Int {
        return R.layout.tsf_editor_item
    }
}