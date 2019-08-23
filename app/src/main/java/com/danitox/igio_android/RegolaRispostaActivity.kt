package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.regola_risposta_activity.*

class RegolaRispostaActivity: AppCompatActivity() {

    lateinit var domandaObject: RegolaDomanda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regola_risposta_activity)

        val realm = Realm.getDefaultInstance()
        val idDomanda = intent.getStringExtra("domandaID")
        this.domandaObject = realm.where(RegolaDomanda::class.java).equalTo("id", idDomanda).findFirst()!!

        this.domandaLabel.text = domandaObject.domanda
        this.editorView.text =  Editable.Factory.getInstance().newEditable(domandaObject.risposta ?: "")
    }


    override fun onPause() {
        super.onPause()
        saveRisposta()
    }

    private fun saveRisposta() {
        val newRisposta = this.editorView.text.toString()

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        domandaObject.risposta = newRisposta
        realm.commitTransaction()
    }
}