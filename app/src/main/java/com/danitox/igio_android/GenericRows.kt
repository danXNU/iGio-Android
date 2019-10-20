package com.danitox.igio_android

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.basic_row.view.*
import kotlinx.android.synthetic.main.basic_row.view.mainLabel
import kotlinx.android.synthetic.main.check_row.view.*
import kotlinx.android.synthetic.main.ms_row.view.*
import kotlinx.android.synthetic.main.switch_row.view.*
import kotlinx.android.synthetic.main.tsf_list_activity.*

class BasicRow(val text: String): Item<ViewHolder>() {
    var id: Int = -1
    var clickAction: (() -> Unit)? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mainLabel.text = text
        viewHolder.itemView.setOnClickListener { clickAction?.invoke() }
    }

    override fun getLayout(): Int {
        return R.layout.basic_row
    }

}

class SpinnerRow(val text: String, var spinnerItems: List<String>, val context: Context, val initialValue: String): Item<ViewHolder>(), AdapterView.OnItemSelectedListener {

    var selectedItemAction: ((Int) -> Unit)? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mainLabel.text = text

        viewHolder.itemView.mainSpinner.onItemSelectedListener = this

        val itemsAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerItems)
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewHolder.itemView.mainSpinner.adapter = itemsAdapter

        val initialValueIndex = spinnerItems.indexOf(initialValue)
        if (initialValueIndex != -1) {
            viewHolder.itemView.mainSpinner.setSelection(initialValueIndex)
        }

    }

    override fun getLayout(): Int {
        return R.layout.ms_row
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedItemAction?.invoke(position)
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}

class SwitchRow(val text: String, val initialValue: Boolean, var switchChangedAction: ((Boolean) -> Unit)? = null): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mainLabel.text = text
        viewHolder.itemView.switchButton.isChecked = initialValue

        viewHolder.itemView.switchButton.setOnCheckedChangeListener { _, isOn ->
            switchChangedAction?.invoke(isOn)
        }
    }

    override fun getLayout(): Int {
        return R.layout.switch_row
    }
}

class CheckRow(val text: String, val initialValue: Boolean, var switchChangedAction: ((Boolean) -> Unit)? = null): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mainLabel.text = text

        viewHolder.itemView.switchButton.setOnCheckedChangeListener { _, isOn ->
            switchChangedAction?.invoke(isOn)
        }
    }

    override fun getLayout(): Int {
        return R.layout.check_row
    }
}