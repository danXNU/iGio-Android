package com.danitox.igio_android

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.telephony.mbms.StreamingServiceInfo
import android.view.MenuItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.compagnia_activity.*
import kotlinx.android.synthetic.main.licenza_row.view.*

class LicenzaItem(var imageID: Int, var title: String)

class LicenzeActivity : AppCompatActivity() {

    var items: List<LicenzaItem> = listOf(
        LicenzaItem(R.drawable.diary, "Diary by SBTS"),
        LicenzaItem(R.drawable.airplane, "Airplane by Shocho"),
        LicenzaItem(R.drawable.search, "Magnifying Glass by Phil Goodwin"),
        LicenzaItem(R.drawable.weightscale, "Weight Scale by Marco Livolsi"),
        LicenzaItem(R.drawable.star, "Star by Mochamad Frans Kurnia"),
        //LicenzaItem(R.drawable.calendar, "Calendar by Alice Design"),
        LicenzaItem(R.drawable.home, "Home by Taqiyyah"),
        LicenzaItem(R.drawable.school_bag, "School Bag by Nociconist"),
        LicenzaItem(R.drawable.social, "Social by Leo"),
        LicenzaItem(R.drawable.verifiche, "Homework by Made")
        //LicenzaItem(R.drawable.settings, "Settings by Royyan Wijaya")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compagnia_activity)
    }

    override fun onResume() {
        super.onResume()
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()

        for (item in items) {
            val newRow = LicenzaRow(item)
            adapter.add(newRow)
        }

        val shouldDisplayDevName = DeveloperEE().shouldDisplayDeveloperName(this)
        if (shouldDisplayDevName) {
            val devRow = BasicRow("App sviluppata da Daniel Bazzani")
            adapter.add(devRow)
        }

        tableView.layoutManager = LinearLayoutManager(this)
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

}

class LicenzaRow(val item: LicenzaItem): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.iconView.setBackgroundResource(item.imageID)
        viewHolder.itemView.mainLabel.text = "${item.title} - (thenounproject.com)"
        viewHolder.itemView.secondaryLabel.text = "Creative Commons License - creativecommons.org/licenses/by/3.0/"
    }

    override fun getLayout(): Int {
        return R.layout.licenza_row
    }
}