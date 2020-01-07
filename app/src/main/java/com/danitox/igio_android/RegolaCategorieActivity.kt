package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
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

        changeTitleForUser()

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
                val newItem = RegolaDomandaRow(domanda, this)
                newSection.add(newItem)
            }
            adapter.add(newSection)
        }

        tableView.adapter = adapter
    }

    private fun changeTitleForUser() {
        val currentUser = UserManager().currentUser()
        when (currentUser.ageScuola) {
            ScuolaType.medie -> title = "Agenda dell'allegria e della santità"
            ScuolaType.biennio -> title = "Il progetto delle 3S"
            ScuolaType.triennio -> title = "Regola di vita"
            else -> title = "Regola di vita"
        }
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


class RegolaDomandaRow(val domanda: RegolaDomanda, val context: Context): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.domanda

        var tempRisposta : String? = domanda.risposta
        if (tempRisposta == null || tempRisposta.isBlank()) { tempRisposta = "Nessuna risposta" }
        viewHolder.itemView.rispostaLabel.text = tempRisposta

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, RegolaRispostaActivity::class.java)
            intent.putExtra("domandaID", domanda.id)
            context.startActivity(intent)
        }
    }

    override fun getLayout(): Int {
        return R.layout.regola_domanda_row
    }

}