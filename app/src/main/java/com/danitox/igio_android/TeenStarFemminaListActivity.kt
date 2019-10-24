package com.danitox.igio_android

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import khronos.beginningOfDay
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.teenstar_m_cell.view.*
import kotlinx.android.synthetic.main.teenstar_m_cell.view.dayLabel
import kotlinx.android.synthetic.main.tsf_list_activity.*
import kotlinx.android.synthetic.main.tsf_list_activity.tableView
import kotlinx.android.synthetic.main.tsf_list_item.view.*
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.*


class TeenStarFemminaListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val months: List<String> = DateFormatSymbols().months.map { it }
    private var years: List<Int> = listOf()


    var selectedMonth: Int = 0
        set(value) {
            field = value
            if (selectedMonth < 0) { return }
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
    var dates: List<Date> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsf_list_activity)

        monthSpinner.onItemSelectedListener = this
        yearSpinner.onItemSelectedListener = this


        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider2 = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        tableView.addItemDecoration(divider)
        tableView.addItemDecoration(divider2)

        refreshSpinners()

        setUp()
    }

    override fun onResume() {
        super.onResume()
        refresh()
        refreshSpinners()
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        if (arg0 == monthSpinner) {
            set(position, null)
        } else {
            set(null, years[position])
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    fun refreshSpinners() {
        val monthsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthsAdapter

        val agent = TSFAgent()
        this.years = agent.getYearsList(agent.getFarestDate())
        val yearsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearsAdapter

        monthSpinner.setSelection(selectedMonth)
        yearSpinner.setSelection(years.indexOf(selectedYear))

    }

    fun refresh() {
        this.fetchItems(currentDate)
    }

    fun fillTableview() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.spanCount = 4

        for (i in 0 until dates.size) {
            var newItem: TSFListItem
            val currDate = dates[i]
            val fitem = allItems.firstOrNull { it.date.beginningOfDay.isCloseToDayEqualTo(currDate.beginningOfDay) }
            if (fitem == null) {
                newItem = TSFListItem(currDate)
                newItem.clickAction = { date, _ ->
                    val intent = Intent(this, TeenStarFemminaEditorActivity::class.java)
                    intent.putExtra("date", date!!.time)
                    this.startActivity(intent)
                }
            } else {
                newItem = TSFListItem(currDate, fitem)
                newItem.clickAction = { _, item ->
                    val intent = Intent(this, TeenStarFemminaEditorActivity::class.java)
                    intent.putExtra("itemID", item!!.id)
                    this.startActivity(intent)
                }
            }
            adapter.add(newItem)
        }


        tableView.layoutManager = GridLayoutManager(this, adapter.spanCount).apply { spanSizeLookup = adapter.spanSizeLookup }
        tableView.adapter = adapter
    }

    fun updateDates() {
        val agent = TSFAgent()
        val range = agent.getMonthRange(currentDate)
        this.dates = agent.getArrayDatesFromRange(range)
    }

    fun fetchItems(date: Date) {
        val agent = TSFAgent()
        this.allItems = agent.fetchItems(date)
        fillTableview()
    }

    fun set(month: Int?, year: Int?) {
        if (month != null) {
            selectedMonth = month
        }
        if (year != null) {
            selectedYear = year
        }
    }

    fun setUp() {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        selectedMonth = month
        selectedYear = year

        monthSpinner.setSelection(month, true)
        yearSpinner.setSelection(years.indexOf(year), true)

    }

}

class TSFListItem(val date: Date, var item: TeenStarFemmina? = null): Item<ViewHolder>() {

    var clickAction: ((Date?, TeenStarFemmina?) -> Unit)? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val cal = Calendar.getInstance()
        cal.time = date

        val day = cal.get(Calendar.DAY_OF_MONTH)

        viewHolder.itemView.dayLabel.text = "$day"

        val backgroundColor = item?.cicloTable?.cicloColor?.getViewColor()
        if (backgroundColor != null) {
            viewHolder.itemView.setBackgroundColor(backgroundColor)
        } else {
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        viewHolder.itemView.setOnClickListener {
            clickAction?.invoke(date, item)
        }
    }
    
    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 4
    }

    override fun getLayout(): Int {
        return R.layout.tsf_list_item
    }
}