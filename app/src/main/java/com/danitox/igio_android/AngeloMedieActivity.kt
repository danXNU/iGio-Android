package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.MenuItem
import android.widget.SeekBar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.angelo_medie_view.*
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_row.view.*

class AngeloMedieActivity: AppCompatActivity() {

    private lateinit var risposteFile: AngeloMedieRisposteFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        risposteFile = AngeloMedieRisposteFile.get(this)

        tableView.layoutManager = LinearLayoutManager(this)

        fillTableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.risposteFile.save(this)
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (key in this.keys) {
            val domanda = domande[key]!!
            val risposta = risposteFile.risposte[key] ?: 0

            val newSection = Section(ToxHeader(key))

            val newRow = AngeloMedieRow(domanda, risposta) {
                risposteFile.risposte[key] = it
            }
            newSection.add(newRow)
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

    private val keys: List<String> = listOf("PREGARE", "GIOCARE", "RACCONTARE", "CORREGGERE")
    private val domande: HashMap<String, String> = hashMapOf(
        Pair("PREGARE", "Pregare per il mio Cliente è un gesto di carità molto alto, posso farlo anche se non lo conosco ancora bene, anche a distanza e anche se la timidezza mi frena ancora un po’. Se prego per lui, lo conosco in una luce nuova e sarò più pronto ad incontrarlo nella quotidianità.\n" +
                "Prego per il mio Cliente?"),
        Pair("GIOCARE", "Giocare insieme è un modo per superare la timidezza, conoscerci e aiutarlo a fare gruppo con altri amici.\n" +
                "Gioco con il mio Cliente?"),
        Pair("RACCONTARE", "Raccontare qualcosa di bello che ho fatto e chiedere a lui di raccontare a me qualcosa è un modo per approfondire il nostro legame e darci buoni consigli e buoni esempi.\n" +
                "Parlo con il mio Cliente?"),
        Pair("CORREGGERE", "Talvolta il mio Cliente avrà bisogno di essere corretto e aiutato a far proprio l’atteggiamento che anche io ho appreso nella casa di Don Bosco.\n" +
                "So correggere con dolcezza?")
    )
}

class AngeloMedieRow(val domanda: String, val risposta: Int, val handler: (Int) -> Unit): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.domandaLabel.text = domanda
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