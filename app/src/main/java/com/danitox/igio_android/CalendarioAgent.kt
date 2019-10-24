package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.widget.Toast

class CalendarioAgent(private val context: Context) {

    fun showCalendario() {
        val agent = SitiLocalizer()
        val sites = agent.fetchLocalWebsites(SitoCategoria.calendario)

        if (sites.isEmpty()) {
            showActivityURL(URLs.calendarioURL.rawValue)
        } else if (sites.size == 1) {
            showActivityURL(sites.first().urlString)
        } else {
            val items : Array<String> = sites.map { it.name }.toTypedArray()

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Quale calendario vuoi raggiungere?")

            builder.setItems(items) { dialog, which ->
                showActivityURL(sites[which].urlString)
                Toast.makeText(this.context, items[which], Toast.LENGTH_LONG).show()
            }

            builder.setNeutralButton("Annulla") { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showActivityURL(url: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        this.context.startActivity(webIntent)
    }

}
