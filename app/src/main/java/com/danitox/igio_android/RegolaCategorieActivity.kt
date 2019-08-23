package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.regola_domanda_row.view.*

class RegolaCategorieActivity : AppCompatActivity() {

    private var regolaVita: RegolaVita? = null

    private lateinit var regolaFetcherModel : RegolaFetcherModel
    private lateinit var scuolaType: ScuolaType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        scuolaType = ScuolaType.none.getFrom(intent.getIntExtra("type", 0))
        regolaFetcherModel = RegolaFetcherModel(this)

        regolaVita = regolaFetcherModel.getLatestRegola(scuolaType)

        tableView.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()
        this.fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (i in 0 until this.regolaVita!!.categorie.size) {
            val categoria = this.regolaVita!!.categorie[i] ?: continue
            val newSection = Section(ToxHeader(categoria.nome))

            for (x in 0 until categoria.domande.size) {
                val domanda = categoria.domande[x] ?: continue
                val newItem = RegolaDomandaRow(domanda)
                newSection.add(newItem)
            }
            adapter.add(newSection)
        }

        tableView.adapter = adapter
    }
}


class RegolaDomandaRow(val domanda: RegolaDomanda): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.domanda
        viewHolder.itemView.rispostaLabel.text = domanda.risposta
    }

    override fun getLayout(): Int {
        return R.layout.regola_domanda_row
    }

}