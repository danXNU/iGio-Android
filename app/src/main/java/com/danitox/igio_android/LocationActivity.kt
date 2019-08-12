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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.lcoation_row.view.*
import kotlinx.android.synthetic.main.locations_activity.*
import kotlinx.android.synthetic.main.locations_activity.tableView

class LocationActivity : AppCompatActivity() {

    private var locationsAdater: LocationsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.locations_activity)

        val locationType = LocationType.none.getFrom(intent.getIntExtra("locType", 0))
        this.locationsAdater = LocationsAdapter(locationType)


        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        this.tableView.addItemDecoration(divider)
        this.tableView.layoutManager = LinearLayoutManager(this)
        this.tableView.adapter = locationsAdater

        locationsAdater?.errorHandler = {
            Snackbar.make(this.tableView, it, Snackbar.LENGTH_SHORT).show()
        }

        locationsAdater?.updateHandler = {
            runOnUiThread {
                locationsAdater?.notifyDataSetChanged()
            }
        }

        locationsAdater?.load()
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
            holder.view.testTickSwitch.visibility = INVISIBLE
        } else {
            if (this.loadingLocations.contains(location)) {
                holder.view.testTickSwitch.visibility = VISIBLE
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
                        list.siti.forEach { print("${it.urlString}") }

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