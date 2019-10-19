package com.danitox.igio_android

import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.lcoation_row.view.*
import kotlinx.android.synthetic.main.locations_activity.*
import kotlinx.android.synthetic.main.locations_activity.tableView

class LocationActivity : AppCompatActivity() {

    private val agent = SitiLocalizer()
    private lateinit var locationType: LocationType
    private var allLocations: List<LocationCodable> = listOf()
    private var loadingLocations: MutableList<LocationCodable> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.locations_activity)

        this.locationType = LocationType.none.getFrom(intent.getIntExtra("locType", 0))


        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        this.tableView.addItemDecoration(divider)
        this.tableView.layoutManager = LinearLayoutManager(this)


        load()
    }

    fun load() {
        agent.getLocations(locationType, saveRecords = true) { codableLocations, error ->
            if (codableLocations != null) {
                this.allLocations = this.agent.updateFromLocal(codableLocations)
                fillTableView()
            }
            if (error != null) {
                showErrror(error)
            }
        }
    }

    fun reloadFromLocal() {
        allLocations = agent.fetchLocalLocations(this.locationType)
        fillTableView()
    }

    fun fillTableView() {
        runOnUiThread {
            val adapter = GroupAdapter<ViewHolder>()

            for (location in this.allLocations) {
                val newRow = LocationItemView(location) { locCodale ->
                    rowClickAction(locCodale)
                }
                adapter.add(newRow)
            }

            tableView.adapter = adapter
        }
    }

    fun rowClickAction(location: LocationCodable) {
        if (location.isSelected) {
            this.agent.removeSites(location)
            this.agent.toggle(location)
            this.reloadFromLocal()
        } else {
            this.loadingLocations.add(location)

            this.agent.fetchLocalizedWebsites(location) { list, error ->
                if (error == null && list != null) {
                    list.siti.forEach { println("${it.urlString}") }

                    this.loadingLocations.clear()
                    agent.toggle(location)
                    this.reloadFromLocal()

                } else {
                    showErrror(error!!)
                }
            }
        }
    }

    fun showErrror(message: String) {
        Snackbar.make(this.tableView, message, Snackbar.LENGTH_LONG).show()
    }
}

class LocationItemView(val location: LocationCodable, val clickAction: ((LocationCodable) -> Unit)? = null): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.locationNameLabel.text = location.name

        if (location.isSelected) {
            viewHolder.itemView.testTickSwitch.visibility = VISIBLE
        } else {
            viewHolder.itemView.testTickSwitch.visibility = INVISIBLE
        }

        viewHolder.itemView.setOnClickListener { clickAction?.invoke(location) }

    }

    override fun getLayout(): Int {
        return R.layout.lcoation_row
    }

}

class LocationsAdapter(locType: LocationType): RecyclerView.Adapter<LocationsViewHolder>() {

    private val agent = SitiLocalizer()

    var updateHandler: (() -> Unit)? = null

    var locationType: LocationType = LocationType.diocesi

    init {
        this.locationType = locType
    }

    var errorHandler: ((String) -> Unit)? = null
        set(value) {
            field = value
            this.agent.errorHandler = value
        }

    private var allLocations: MutableList<LocationCodable> = mutableListOf()
        set(value) {
            if (value.isEmpty()) { return }
            field = value
            updateHandler?.invoke()
        }

    var loadingLocations: MutableList<LocationCodable> = mutableListOf()
        set(value) {
            field = value
            updateHandler?.invoke()
        }


    fun load() {
        this.allLocations.clear()
        agent.getLocations(locationType, saveRecords = true) { codableLocations, error ->
            if (codableLocations != null) {
                this.allLocations = this.agent.updateFromLocal(codableLocations)
            }
            if (error != null) {
                Log.e("locations", error)
            }
        }
    }

    fun reloadFromLocal() {
        allLocations.clear()
        allLocations = agent.fetchLocalLocations(this.locationType)
    }

    override fun getItemCount(): Int {
        return allLocations.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, veiwType: Int): LocationsViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val row = inflator.inflate(R.layout.lcoation_row, parent, false)
        return LocationsViewHolder(row)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val location = allLocations[position]

        holder.view.locationNameLabel.text = location.name

        if (location.isSelected) {
            holder.view.testTickSwitch.visibility = VISIBLE
        } else {
            if (this.loadingLocations.contains(location)) {
                holder.view.testTickSwitch.visibility = INVISIBLE
            } else {
                holder.view.testTickSwitch.visibility = INVISIBLE
            }
        }

        holder.location = this.allLocations[position]
        holder.onClickAction = { location ->
            if (location.isSelected) {
                this.agent.removeSites(location)
                this.agent.toggle(location)
                this.reloadFromLocal()
            } else {
                this.loadingLocations.add(location)

                this.agent.fetchLocalizedWebsites(location) { list, error ->
                    if (error == null && list != null) {
                        list.siti.forEach { println("${it.urlString}") }

                        this.loadingLocations.clear()
                        agent.toggle(location)
                        this.reloadFromLocal()

                    } else {
                        this.errorHandler?.invoke(error!!)
                    }
                }
            }
        }
    }


}


class LocationsViewHolder(val view: View, var location: LocationCodable? = null, var onClickAction: ((LocationCodable) -> Unit)? = null): RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {
            onClickAction?.invoke(location!!)
        }
    }

}