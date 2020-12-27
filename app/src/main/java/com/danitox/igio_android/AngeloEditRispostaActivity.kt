package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.MenuItem
import kotlinx.android.synthetic.main.regola_domanda_row.*
import kotlinx.android.synthetic.main.regola_risposta_activity.*
import kotlinx.android.synthetic.main.regola_risposta_activity.domandaLabel
import java.util.*

class AngeloEditRispostaActivity: AppCompatActivity() {

    lateinit var risposteFile: AngeloRispostaFile
    lateinit var domandaID: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regola_risposta_activity)

        val strID = this.intent.getStringExtra("domandaID")
        if (strID != null) {
            domandaID = UUID.fromString(strID)
        } else { domandaID = UUID.randomUUID() } //set a random ID so it won't be saved

        risposteFile = AngeloRispostaFile.get(this)

        this.domandaLabel.text = this.intent.getStringExtra("domandaStr") ?: ""
        this.editorView.text =  Editable.Factory.getInstance().newEditable(risposteFile.risposte[domandaID] ?: "")

    }

    override fun onPause() {
        super.onPause()
        risposteFile.risposte[domandaID] = this.editorView.text.toString()
        risposteFile.save(this)
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