package com.danitox.igio_android

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.compagnia_activity.*

class DebugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        val row1 = BasicRow("Resetta Regola di Vita")
        row1.clickAction = {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.delete(RegolaVita::class.java)
            realm.delete(RegolaCategoria::class.java)
            realm.delete(RegolaDomanda::class.java)
            realm.commitTransaction()
            Snackbar.make(this.tableView, "La regola di vita è stata resettata", Snackbar.LENGTH_SHORT).show()
        }

        val row2 = BasicRow("Resetta TeenSTAR")
        row2.clickAction = {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()

            realm.delete(TeenStarMaschio::class.java)
            realm.delete(TeenStarFemmina::class.java)

            realm.commitTransaction()
            Snackbar.make(this.tableView, "TeenSTAR è stato resettato", Snackbar.LENGTH_SHORT).show()
        }

        val row3 = BasicRow("Resetta 'Il mio percorso formativo'")
        row3.clickAction = {

            val agent = CompagniaAgent(this)
            agent.removeAll()
            agent.createIfNotPresent()

            Snackbar.make(this.tableView, "Percorso formativo resettato", Snackbar.LENGTH_SHORT).show()

        }

        val row4 = BasicRow("Resetta GioProNet")
        row4.clickAction = {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()

            realm.delete(GioProNet::class.java)
            realm.delete(GioProNetTask::class.java)

            realm.commitTransaction()
            Snackbar.make(this.tableView, "GioProNet resettato", Snackbar.LENGTH_SHORT).show()
        }

        adapter.add(row1)
        adapter.add(row2)
        adapter.add(row3)
        adapter.add(row4)

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = adapter
    }

}