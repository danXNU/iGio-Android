package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.lcoation_row.view.*
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

    override fun onResume() {
        super.onResume()
        risposteFile = AngeloRispostaFile.get(this)
        fillTableView()
    }

    override fun onPause() {
        super.onPause()
        risposteFile.save(this)
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
            val row = AngeloDomandaRow(domanda, risposta, this)
            risposteSection.add(row)
        }


        val paroleSection = Section(ToxHeader("Flagga i verbi dell’Angelo Custode in cui ti riconosci e in cui ti stai impegnando:"))
        for (parola in domandeFile.parole) {
            val isSelcted = risposteFile.paroleChecked[parola.id] ?: false
            val row = AngeloParolaCell(parola, isSelcted, clickAction = {
                updateParola(it)
            })
            paroleSection.add(row)
        }

        adapter.add(risposteSection)
        adapter.add(paroleSection)

        tableView.adapter = adapter

    }

    fun updateParola(parola: AngeloDomandeFile.Item) {
        val currentValue = this.risposteFile.paroleChecked[parola.id] ?: false
        this.risposteFile.paroleChecked[parola.id] = !currentValue
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

class AngeloDomandaRow(val domanda: AngeloDomandeFile.Item, val risposta: String, val context: Context): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda.str

        var tempRisposta : String? = risposta
        if (tempRisposta == null || tempRisposta.isBlank()) { tempRisposta = "Nessuna risposta" }
        viewHolder.itemView.rispostaLabel.text = tempRisposta

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, AngeloEditRispostaActivity::class.java)
            intent.putExtra("domandaStr", domanda.str)
            intent.putExtra("domandaID", domanda.id.toString())
            context.startActivity(intent)
        }
    }

    override fun getLayout(): Int {
        return R.layout.regola_domanda_row
    }

}

class AngeloParolaCell(var parola: AngeloDomandeFile.Item, var isSelected: Boolean, val clickAction: ((AngeloDomandeFile.Item) -> Unit)? = null): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.locationNameLabel.text = parola.str

        showCheck(isSelected, viewHolder)

        viewHolder.itemView.setOnClickListener {
            clickAction?.invoke(parola)
            showCheck(!isSelected, viewHolder)
        }

    }

    fun showCheck(show: Boolean, viewHolder: ViewHolder) {
        if (show) {
            viewHolder.itemView.checkImage.visibility = View.VISIBLE
        } else {
            viewHolder.itemView.checkImage.visibility = View.INVISIBLE
        }
    }

    override fun getLayout(): Int {
        return R.layout.lcoation_row
    }

}