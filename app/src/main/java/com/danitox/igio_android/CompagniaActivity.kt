package com.danitox.igio_android

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
    private var verifica: VerificaCompagnia? = null
    private var storage : RealmList<VerificaCategoria> = RealmList()

    private lateinit var type: ScuolaType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        type = ScuolaType.none.getFrom(intent.getIntExtra("type", 0))

        model = CompagniaAgent(this)
        model.createIfNotPresent()

        verifica = model.getLatestVerifica(type)
        storage = verifica!!.categorie

        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()

    }

    private fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0 until storage.size) {
            val categoriaObj = storage[i] ?: continue
            val newSection = Section(ToxHeader(categoriaObj.name))

            for (x in 0 until categoriaObj.domande.size) {
                val domanda = categoriaObj.domande[x] ?: continue

                val newRow = CompagniaRow(domanda)
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


class CompagniaRow(val domanda: VerificaDomanda): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.domanda
        viewHolder.itemView.domandaSlider.progress = domanda.risposta

        viewHolder.itemView.currentValueLabel.text = "${viewHolder.itemView.domandaSlider.progress}"

        viewHolder.itemView.domandaSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val realm = domanda.realm
                realm.beginTransaction()
                domanda.risposta = seekBar?.progress ?: 0
                realm.commitTransaction()


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