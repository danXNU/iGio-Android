package com.danitox.igio_android

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.SeekBar
import com.xwray.groupie.*
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_activity.tableView
import kotlinx.android.synthetic.main.compagnia_row.view.*
import kotlinx.android.synthetic.main.section_header.view.*
import kotlin.math.round

class CompagniaActivity: AppCompatActivity() {

    private lateinit var model : CompagniaAgent
    private lateinit var domandeFile : CompagniaDomandeFile
    private var risposteFile: CompagniaRisposteFile? = null

    private lateinit var type: ScuolaType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        type = ScuolaType.none.getFrom(intent.getIntExtra("type", 0))

        model = CompagniaAgent(this)

        //if still not converted to json, convert!
        val preferences = this.getSharedPreferences("iGio", Context.MODE_PRIVATE)
        val isConvertedToJSON = preferences.getBoolean("isVerificaRealmToJSONConversionDone", false)

        if (isConvertedToJSON == false) {
            model.convertRealmToJSON()
            val editor = preferences.edit()
            editor.putBoolean("isVerificaRealmToJSONConversionDone", true)
            editor.apply()
        }
        //fine conversione

        domandeFile = CompagniaDomandeFile().get(type, this@CompagniaActivity)
        risposteFile = CompagniaRisposteFile.get(type, this)


        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()

    }

    override fun onDestroy() {
        super.onDestroy()
        this.risposteFile?.save(type, this)
    }

    private fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0 until domandeFile.categorie.size) {
            val categoriaObj = domandeFile.categorie[i]
            val newSection = Section(ToxHeader(categoriaObj.name))

            for (x in 0 until categoriaObj.domande.size) {
                val domanda = categoriaObj.domande[x]

                val newRow = CompagniaRow(domanda, risposteFile?.risposte?.get(domanda.id) ?: 0) {
                    if (risposteFile != null) {
                        risposteFile!!.risposte[domanda.id] = it
                    }
                }
                newSection.add(newRow)
            }
            adapter.add(newSection)
        }

        tableView.adapter = adapter
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


class CompagniaRow(val domanda: CompagniaDomandeFile.CompagniaDomanda, val risposta: Int, val handler: (Int) -> Unit): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.str
        viewHolder.itemView.domandaSlider.progress = risposta

        viewHolder.itemView.currentValueLabel.text = "${viewHolder.itemView.domandaSlider.progress}"

        viewHolder.itemView.domandaSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handler.invoke(seekBar?.progress ?: 0)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewHolder.itemView.currentValueLabel.text = "${seekBar?.progress ?: 0 }"
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.compagnia_row
    }

}


class ToxHeader(val title: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.section_header
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.headerTitleLabel.text = title
    }
}