package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.regola_domanda_row.view.*

class AngeloActivity: AppCompatActivity() {

    lateinit var domandeFile: AngeloDomandeFile
    lateinit var risposteFile: AngeloRispostaFile

    var preghiera: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        domandeFile = AngeloDomandeFile.get(this)
        risposteFile = AngeloRispostaFile.get(this)

        val file = this.assets.open("preghiera_angelo_custode.txt")
        this.preghiera = String(file.readBytes())
        file.close()

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        risposteFile.save(this)
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        val risposteSection = Section(ToxHeader("Domande"))
        for (domanda in domandeFile.domande) {
            val risposta = risposteFile.risposte[domanda.id] ?: ""
            val row = AngeloDomandaRow(domanda.str, risposta, this)
            risposteSection.add(row)
        }

        adapter.add(risposteSection)
        tableView.adapter = adapter

    }

}

class AngeloDomandaRow(val domanda: String, val risposta: String, val context: Context): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda

        var tempRisposta : String? = risposta
        if (tempRisposta == null || tempRisposta.isBlank()) { tempRisposta = "Nessuna risposta" }
        viewHolder.itemView.rispostaLabel.text = tempRisposta

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, RegolaRispostaActivity::class.java)
            //intent.putExtra("domandaID", domanda.id)
            context.startActivity(intent)
        }
    }

    override fun getLayout(): Int {
        return R.layout.regola_domanda_row
    }

}