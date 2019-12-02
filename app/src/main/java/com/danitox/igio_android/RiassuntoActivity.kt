package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.compagnia_activity.tableView
import kotlinx.android.synthetic.main.tsm_list.*

class RiassuntoActivity: AppCompatActivity() {

    private var regolaVita: RegolaVita? = null

    private lateinit var regolaFetcherModel : RegolaFetcherModel
    private lateinit var scuolaType: ScuolaType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.riassunto_view)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        scuolaType = ScuolaType.none.getFrom(intent.getIntExtra("type", 0))
        regolaFetcherModel = RegolaFetcherModel(this)

        regolaVita = regolaFetcherModel.getLatestRegola(scuolaType)

        tableView.layoutManager = LinearLayoutManager(this)


        edit_button.setOnClickListener {
            val intent = Intent(this, RegolaCategorieActivity::class.java)
            intent.putExtra("type", UserManager().currentUser().ageScuola.value)
            this.startActivity(intent)
        }

        //fillTableView()
    }


    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        val domandeRiassunto = regolaVita!!.domandeRiassunto

        for (i in 0 until domandeRiassunto.size) {
            val domandaRow = regolaVita!!.domandeRiassuntoRaw[i]
            val categoria = domandeRiassunto[i].categoria?.first()
            val newSection = Section(ToxHeader(categoria!!.nome))

            val newRow = RegolaDomandaRow(categoria.domande.filter { it.order == domandaRow.domandaIndex }.first(), this)
            newSection.add(newRow)

            adapter.add(newSection)
        }

        tableView.adapter = adapter
    }

}