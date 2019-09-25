package com.danitox.igio_android

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import android.widget.DatePicker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import khronos.toString
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.gpn_edit_row.view.*
import java.util.*

class GioProEditorActivity : AppCompatActivity() {

    lateinit var gioItem: GioProNet
    val cal = Calendar.getInstance()

    private val taskClicked: (TaskType, GioProTime) -> Unit = { task, orario ->
        setTask(task, orario)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.compagnia_activity)

        val itemID = intent.getStringExtra("itemID")
        if (itemID == null) {
            gioItem = GioProNet()
            gioItem.date = Date()
        } else {
            val realm = Realm.getDefaultInstance()
            gioItem = realm.where(GioProNet::class.java).equalTo("id", itemID).findFirst()!!
        }

    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {

                cal.set(year, monthOfYear, dayOfMonth)
                dateDidChange(cal.time)
            }
        }

        val adapter = GroupAdapter<ViewHolder>()
        val dateSection = Section(ToxHeader(gioItem.date.toString("EEEE - dd/MM/yyyy")))
        val changeDateRow = DateRow("Modifica la data") {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dateSection.add(changeDateRow)
        adapter.add(dateSection)


        for (orario in GioProTime.values()) {
            if (orario == GioProTime.none) { continue }
            val newSection = Section(ToxHeader("Cosa ho fatto alle ${orario.stringValue()}"))
            val editingRow = GPNEditRow(orario, gioItem.getTask(orario).taskType, taskClicked)
            newSection.add(editingRow)
            adapter.add(newSection)
        }

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }

    fun setTask(task: TaskType, time: GioProTime) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        gioItem.getTask(time).taskType = task
        realm.commitTransaction()

        fillTableView()
    }

    fun dateDidChange(date: Date) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        gioItem.date = date
        realm.commitTransaction()

        fillTableView()
    }
}


class GPNEditRow(val orario: GioProTime, val initialTask: TaskType?, val taskTapped: (TaskType, GioProTime) -> Unit): Item<ViewHolder>() {

    private lateinit var tasksButtons: Map<TaskType, Button>

    override fun bind(viewHolder: ViewHolder, position: Int) {
        tasksButtons = hashMapOf(
            Pair(TaskType.whatsapp, viewHolder.itemView.waButton),
            Pair(TaskType.gaming, viewHolder.itemView.gamingButton),
            Pair(TaskType.amici, viewHolder.itemView.amiciButton),
            Pair(TaskType.libri, viewHolder.itemView.libriButton),
            Pair(TaskType.sport, viewHolder.itemView.sportButton),
            Pair(TaskType.riposo, viewHolder.itemView.riposoButton)
        )

        viewHolder.itemView.waButton.setOnClickListener { taskTapped.invoke(TaskType.whatsapp, orario) }
        viewHolder.itemView.gamingButton.setOnClickListener { taskTapped.invoke(TaskType.gaming, orario) }
        viewHolder.itemView.amiciButton.setOnClickListener { taskTapped.invoke(TaskType.amici, orario) }
        viewHolder.itemView.libriButton.setOnClickListener { taskTapped.invoke(TaskType.libri, orario) }
        viewHolder.itemView.sportButton.setOnClickListener { taskTapped.invoke(TaskType.sport, orario) }
        viewHolder.itemView.riposoButton.setOnClickListener { taskTapped.invoke(TaskType.riposo, orario) }


        if (initialTask != null) {
            setTask(initialTask)
        }

    }

    override fun getLayout(): Int {
        return R.layout.gpn_edit_row
    }

    fun setTask(task: TaskType) {
        tasksButtons.values.forEach { it.setBackgroundColor(Color.TRANSPARENT) }
        tasksButtons[task]?.setBackgroundColor(Color.GREEN)
    }
}
