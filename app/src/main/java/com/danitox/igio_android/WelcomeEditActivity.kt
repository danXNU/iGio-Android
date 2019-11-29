package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.Realm
import kotlinx.android.synthetic.main.compagnia_activity.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.compagnia_activity.tableView
import kotlinx.android.synthetic.main.welcome_edit_page.*
import java.util.*
import java.util.Arrays.asList



class WelcomeEditActivity: AppCompatActivity() {
    var notificheModel = Notifiche()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.welcome_edit_page)

        tableView.layoutManager = LinearLayoutManager(this)

        edit_button.setOnClickListener {
            SavedValues().setIsFirstLaunch(false, this)
            finish()
        }

        //fillTableView()
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

        val row3 = SwitchRow("Attiva le notifiche", notificheModel.areNotificheActive(this)) {
            if (it) {
                val builder = AlertDialog.Builder(this@WelcomeEditActivity)

                val notificheStrings : Array<String> = Notifiche.NotificheType.none.allStrings().map { it }.toTypedArray()
                val checkedNotifiche = Notifiche.NotificheType.none.allBooleanValues(this)

                val notificheList = Arrays.asList(notificheStrings)

                builder.setMultiChoiceItems(notificheStrings, checkedNotifiche) { dialog, which, isChecked ->
                    checkedNotifiche[which] = isChecked
                }

                builder.setCancelable(false)
                builder.setTitle("Attiva le notifiche")

                builder.setPositiveButton("OK") { dialog, which ->
                    for (i in checkedNotifiche.indices) {
                        val checked = checkedNotifiche[i]

                        val notificaType = Notifiche.NotificheType.none.getFromString(notificheList[0][i])
                        notificheModel.setNotificaTypeActive(notificaType, checked, this)
                    }

                    val selectedNotificheCount = checkedNotifiche.filter { it == true }.size
                    notificheModel.setAreNotificheActive(selectedNotificheCount > 0, this)

                    notificheModel.updateStatus(this)
                    fillTableView()
                }

                builder.setNeutralButton("Annulla") { dialog, which ->
                    fillTableView()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }

        /*val row4 = BasicRow("Notifiche").apply {
            id = 6
            clickAction = {

                val builder = AlertDialog.Builder(this@WelcomeEditActivity)

                // String array for alert dialog multi choice items
                val notificheStrings : Array<String> = Notifiche.NotificheType.none.allStrings().map { it }.toTypedArray()

                // Boolean array for initial selected items
                val checkedNotifiche = booleanArrayOf(
                    false,
                    false,
                    false,
                    false,
                    false
                )

                val notificheList = Arrays.asList(notificheStrings)

                builder.setMultiChoiceItems(notificheStrings, checkedNotifiche) { dialog, which, isChecked ->
                    checkedNotifiche[which] = isChecked
                }

                builder.setCancelable(false)
                builder.setTitle("Attiva le notifiche")

                builder.setPositiveButton("OK") { dialog, which ->
                    for (i in checkedNotifiche.indices) {
                        val checked = checkedNotifiche[i]
                        if (checked) {
                            val notificaActivated = notificheList.get(i)

                        }
                    }
                }

                builder.setNeutralButton("Annulla", DialogInterface.OnClickListener { dialog, which ->
                    // Do something when click the neutral button
                })

                val dialog = builder.create()
                dialog.show()
            }
        }*/


        userSection.add(row1)
        userSection.add(row2)
        userSection.add(row3)



        adapter.add(userSection)

        tableView.adapter = adapter
    }
}