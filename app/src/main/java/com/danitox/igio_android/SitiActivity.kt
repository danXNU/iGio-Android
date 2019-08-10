package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.sito_row.view.*

class SitiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.siti_activity)

    }
}

class SitiAdapter(sitiCategorie: MutableList<SitoCategoria>): RecyclerView.Adapter<SitiViewHolder>() {

    var sites: MutableList<SitoObject> = mutableListOf()

    val agent: SitiLocalizer = SitiLocalizer()
    private var categorie: MutableList<SitoCategoria> = mutableListOf()

    var updateHandler: ((String) -> Unit)? = null

    //var hasAlreadyRequestedSitesWhileEmpty: Boolean = false

    init {
        this.categorie = sitiCategorie
    }

    fun fetchLocalWebsites() {
        this.sites.clear()
        for (categoria in this.categorie) {
            this.sites.addAll(agent.fetchLocalWebsites(categoria))
        }
    }

    fun updateFromServer() {
        //TODO: da finire di implementare. Da aggiungere le Locations e tutto il resto.
    }

    override fun getItemCount(): Int {
        return this.sites.size
    }


    override fun onBindViewHolder(holder: SitiViewHolder, position: Int) {
        val sito = this.sites[position]
        holder.view.sitoNameLabel.text = sito.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitiViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.sito_row, parent, false)
        return SitiViewHolder(cell)
    }

}


class SitiViewHolder(val view: View): RecyclerView.ViewHolder(view)