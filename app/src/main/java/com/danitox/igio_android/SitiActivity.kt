package com.danitox.igio_android

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.locations_activity.tableView
import kotlinx.android.synthetic.main.siti_activity.*
import kotlinx.android.synthetic.main.sito_row.view.*

class SitiActivity : AppCompatActivity() {

    private var sitesAdapter: SitiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.siti_activity)

        this.sitesAdapter = SitiAdapter(mutableListOf(SitoCategoria.materiali, SitoCategoria.preghiere)) { sito ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(sito.urlString)
            this.startActivity(intent)
        }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        this.tableView.addItemDecoration(divider)
        this.tableView.layoutManager = LinearLayoutManager(this)
        this.tableView.adapter = sitesAdapter

        this.sitesAdapter?.updateHandler = {
            runOnUiThread {
                this.sitesAdapter?.notifyDataSetChanged()
            }
        }

        this.sitesAdapter?.errorHandler = {
            Log.e("Sites", it)
        }

        this.reloadButton.setOnClickListener {
            this.sitesAdapter?.updateFromServer()
        }

    }

    override fun onResume() {
        super.onResume()
        sitesAdapter?.fetchLocalWebsites()
    }
}

class SitiAdapter(private val sitiCategorie: MutableList<SitoCategoria>, val clickAction: ((SitoObject) -> Unit)?): RecyclerView.Adapter<SitiViewHolder>() {

    var sites: MutableList<SitoObject> = mutableListOf()

    val agent: SitiLocalizer = SitiLocalizer()

    var updateHandler: (() -> Unit)? = null
    var errorHandler: ((String) -> Unit)? = null

    fun fetchLocalWebsites() {
        this.sites.clear()
        for (categoria in this.sitiCategorie) {
            this.sites.addAll(agent.fetchLocalWebsites(categoria))
        }
        this.updateHandler?.invoke()
    }

    fun updateFromServer() {
        agent.fetchAllWebsites { list, error ->
            if (error == null && list != null) {
                this.sites = list.siti.filter { this.sitiCategorie.contains(it.type) }.toMutableList()
                this.updateHandler?.invoke()
            } else {
                this.errorHandler?.invoke(error!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.sites.size
    }

    override fun onBindViewHolder(holder: SitiViewHolder, position: Int) {
        val sito = this.sites[position]
        holder.view.sitoNameLabel.text = sito.name

        holder.sitoObject = this.sites[position]
        holder.clickAction = this.clickAction
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitiViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.sito_row, parent, false)
        return SitiViewHolder(cell)
    }

}


class SitiViewHolder(val view: View, var sitoObject: SitoObject? = null, var clickAction: ((SitoObject) -> Unit)? = null): RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {
            if (sitoObject != null) {
                clickAction?.invoke(sitoObject!!)
            }
        }
    }

}