package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*

class NotificheActivity : AppCompatActivity() {

    var model = Notifiche()
    var areNotificheActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_view)
    }

    override fun onResume() {
        super.onResume()

        Notifiche().updateStatus(this)
        refresh()
    }

    fun refresh() {
        areNotificheActive = model.areNotificheActive(this)
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()
        val allowNotificheRow = SwitchRow("Attiva le notifiche", areNotificheActive) { isOn ->
            model.setAreNotificheActive(isOn, this)
            refresh()
        }
        adapter.add(allowNotificheRow)

        val notificheTypeSection = Section(ToxHeader("Tipo di notifiche"))
        for (type in Notifiche.NotificheType.values().filter { it != Notifiche.NotificheType.none }) {
            val isNotificaTyeActive = model.getActiveNotifiche(this).contains(type)
            val newRow = SwitchRow("${type.stringValue()}", isNotificaTyeActive) { notificheRowClicked(type, it) }
            notificheTypeSection.add(newRow)
        }
        if (model.areNotificheActive(this)) {
            adapter.add(notificheTypeSection)
        }


        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }

    fun notificheRowClicked(type: Notifiche.NotificheType, isOn: Boolean) {
        if (!model.areNotificheActive(this)) { return }

        model.setNotificaTypeActive(type, isOn, this)
        model.updateStatus(this)
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