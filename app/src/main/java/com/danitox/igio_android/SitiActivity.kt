package com.danitox.igio_android

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.locations_activity.tableView
import kotlinx.android.synthetic.main.siti_activity.*
import kotlinx.android.synthetic.main.sito_row.view.*
import kotlinx.android.synthetic.main.social_layout.*

class SitiActivity : Fragment() {

    val sitiCategorie : List<SitoCategoria> = listOf(SitoCategoria.materiali, SitoCategoria.preghiere)

    val sitiAgent: SitiLocalizer = SitiLocalizer()
    var allSites: List<SitoObject> = listOf()

    var isFirstRun: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        this.tableView.addItemDecoration(divider)
        this.tableView.layoutManager = LinearLayoutManager(this.context)

        updateFromServer()
    }


    override fun onResume() {
        super.onResume()
        if (isFirstRun) {
            isFirstRun = false
        } else {
            fetchLocalWebsites()
        }

    }

    fun fetchLocalWebsites() {
        val list : MutableList<SitoObject> = mutableListOf()
        for (categoria in this.sitiCategorie) {
            list.addAll(sitiAgent.fetchLocalWebsites(categoria))
        }
        this.allSites = list.toList()
        fillTableview()
    }

    fun updateFromServer() {
        sitiAgent.fetchAllWebsites { list, error ->
            if (error == null && list != null) {
                this.allSites = list.siti.filter { this.sitiCategorie.contains(it.type) }.toList()
                fillTableview()
            } else {
                showError(error!!)
            }
        }
    }

    fun fillTableview() {
        this.activity?.runOnUiThread {
            val adapter = GroupAdapter<ViewHolder>()

            //val mainSection = Section(ToxHeader("Risorse"))
            val mainSection = Section()
            for (sito in allSites) {
                val newRow = SitoItem(sito) { sitoClickedAction(it) }
                mainSection.add(newRow)
            }

            adapter.add(mainSection)
            tableView.adapter = adapter
        }
    }

    fun sitoClickedAction(sito: SitoObject) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(sito.urlString)
        this.startActivity(intent)
    }

    fun showError(message: String) {
        if (this.tableView != null) {
            Snackbar.make(this.tableView, message, Snackbar.LENGTH_LONG).show()
        }

    }
}

class SitoItem(val sito: SitoObject, var clickAction: ((SitoObject) -> Unit)?): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.sitoNameLabel.text = sito.name

        viewHolder.itemView.setOnClickListener { clickAction?.invoke(sito) }
    }

    override fun getLayout(): Int {
        return R.layout.sito_row
    }
}