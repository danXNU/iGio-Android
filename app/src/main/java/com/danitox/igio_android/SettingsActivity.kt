package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.compagnia_activity.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsm_list)

        tableView.layoutManager = LinearLayoutManager(this)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)
    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        val userSection = Section(ToxHeader("Utente"))
        val row1 = SpinnerRow("Età", ScuolaType.none.stringValues(), this, UserManager().currentUser().ageScuola.stringValue)
        row1.selectedItemAction = { index ->
            val allItems = ScuolaType.values().filter { it != ScuolaType.none }

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            UserManager().currentUser().ageScuola = allItems[index]
            realm.commitTransaction()
        }

        val row2 = SpinnerRow("Maschio/Femmina", UserGender.none.stringValues(), this, UserManager().currentUser().gender.stringValue)
        row2.selectedItemAction = { index ->
            val allItems = UserGender.values().filter { it != UserGender.none }

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            UserManager().currentUser().gender = allItems[index]
            realm.commitTransaction()
        }


        val row3 = BasicRow("Provincia & città").apply {
                id = 3
                clickAction = {

                }
        }

            userSection.add(row1)
            userSection.add(row2)
            userSection.add(row3)

            val generalSection = Section(ToxHeader("Generale"))
            val row4 = BasicRow("Info").apply {
                id = 4
                clickAction = {

                }
            }
            val row5 = BasicRow("Notifiche").apply {
                id = 5
                clickAction = {

                }
            }
            val row7 = BasicRow("Licenze").apply {
                id = 7
                clickAction = {

                }
            }
            val row8 = BasicRow("Debug").apply {
                id = 8
                clickAction = {

                }
            }

            generalSection.add(row4)
            generalSection.add(row5)
            generalSection.add(row7)
            generalSection.add(row8)

            adapter.add(userSection)
            adapter.add(generalSection)
            tableView.adapter = adapter
        }
}