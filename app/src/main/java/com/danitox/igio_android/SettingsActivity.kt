package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.compagnia_activity.*

class SettingsActivity : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tableView.layoutManager = LinearLayoutManager(this.context)

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)
    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        val userSection = Section(ToxHeader("Utente"))
        val row1 = SpinnerRow("Età", ScuolaType.none.stringValues(), this.context!!, UserManager().currentUser().ageScuola.stringValue)
        row1.selectedItemAction = { index ->
            val allItems = ScuolaType.values().filter { it != ScuolaType.none }

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            UserManager().currentUser().ageScuola = allItems[index]
            realm.commitTransaction()
        }

        val row2 = SpinnerRow("Maschio/Femmina", UserGender.none.stringValues(), this.context!!, UserManager().currentUser().gender.stringValue)
        row2.selectedItemAction = { index ->
            val allItems = UserGender.values().filter { it != UserGender.none }

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            UserManager().currentUser().gender = allItems[index]
            realm.commitTransaction()
        }


        val row3 = BasicRow("Provincia").apply {
                id = 3
                clickAction = {
                    val intent = Intent(this@SettingsActivity.context, LocationActivity::class.java)
                    intent.putExtra("locType", LocationType.diocesi.value)
                    this@SettingsActivity.startActivity(intent)
                }
        }
        val row4 = BasicRow("Città").apply {
            id = 4
            clickAction = {
                val intent = Intent(this@SettingsActivity.context, LocationActivity::class.java)
                intent.putExtra("locType", LocationType.city.value)
                this@SettingsActivity.startActivity(intent)
            }
        }

        userSection.add(row1)
        userSection.add(row2)
        userSection.add(row3)
        userSection.add(row4)

        val generalSection = Section(ToxHeader("Generale"))
        val row5 = BasicRow("Info").apply {
            id = 5
            clickAction = {
                val intent = Intent(this@SettingsActivity.context, InfoActivity::class.java)
                this@SettingsActivity.startActivity(intent)
            }
        }
        val row6 = BasicRow("Notifiche").apply {
            id = 6
            clickAction = {
                val intent = Intent(this@SettingsActivity.context, NotificheActivity::class.java)
                this@SettingsActivity.startActivity(intent)
            }
        }
        val row7 = BasicRow("Licenze").apply {
            id = 7
            clickAction = {
                val intent = Intent(this@SettingsActivity.context, LicenzeActivity::class.java)
                this@SettingsActivity.startActivity(intent)
            }
        }
        /*val row8 = BasicRow("Debug").apply {
            id = 8
            clickAction = {
                val intent = Intent(this@SettingsActivity.context, DebugActivity::class.java)
                this@SettingsActivity.startActivity(intent)
            }
        }*/

        generalSection.add(row5)
        generalSection.add(row6)
        generalSection.add(row7)
        //generalSection.add(row8)

        adapter.add(userSection)
        adapter.add(generalSection)
        tableView.adapter = adapter
    }
}