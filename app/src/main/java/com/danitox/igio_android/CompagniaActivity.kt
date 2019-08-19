package com.danitox.igio_android

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.realm.RealmList
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_row.view.*

class CompagniaActivity: AppCompatActivity() {

    private lateinit var model : CompagniaAgent
    private var verifica: VerificaCompagnia? = null
    private var storage : RealmList<VerificaCategoria> = RealmList()

    private lateinit var type: ScuolaType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        type = ScuolaType.none.getFrom(intent.getIntExtra("type", 0))

        model = CompagniaAgent(this)
        model.createIfNotPresent()

        verifica = model.getLatestVerifica(type)
        storage = verifica!!.categorie

        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()

    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0..storage.size) {
            val newSection = Section()
            val categoriaObj = storage[i]
            if (categoriaObj == null) { continue }

            for (x in 0..categoriaObj.domande.size) {
                val domanda = categoriaObj.domande[x]
                if (domanda == null) { continue }
                val newRow = CompagniaRow(domanda)
                newSection.add(newRow)
            }
            adapter.add(newSection)
        }

        tableView.adapter = adapter
    }

}


class CompagniaRow(val domanda: VerificaDomanda): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.domanda
        viewHolder.itemView.domandaSlider.progress = domanda.risposta
    }

    override fun getLayout(): Int {
        return R.layout.compagnia_row
    }

}
