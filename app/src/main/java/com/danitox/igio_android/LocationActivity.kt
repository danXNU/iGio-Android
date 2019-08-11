package com.danitox.igio_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.locations_activity.*

class LocationActivity : AppCompatActivity() {

    val locationsAdater: LocationsAdapter = LocationsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.locations_activity)

        this.tableView.layoutManager = LinearLayoutManager(this)
        this.tableView.adapter = locationsAdater
    }
}

class LocationsAdapter: RecyclerView.Adapter<LocationsViewHolder>() {

    val agent = SitiLocalizer()

    var updateHandler: (() -> Unit)? = null

    var locationType: LocationType = LocationType.diocesi

    var errorHandler: ((String) -> Unit)? = null
        set(value) {
            field = value
            this.agent.errorHandler = value
        }

    var allLocations: MutableList<LocationCodable> = mutableListOf()
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}


class LocationsViewHolder(val view: View): RecyclerView.ViewHolder(view)