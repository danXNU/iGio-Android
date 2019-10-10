package com.danitox.igio_android

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.social_layout.*

class SocialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_layout)

        facebookButton.setOnClickListener {
            tapped(SitoCategoria.facebook)
        }

        instagramButton.setOnClickListener {
            tapped(SitoCategoria.instagram)
        }

        youtubeButton.setOnClickListener {
            tapped(SitoCategoria.youtube)
        }
    }


    fun fetchWebsites(categoria: SitoCategoria): List<SitoObject> {
        val agent = SitiLocalizer()
        return agent.fetchLocalWebsites(categoria)
    }

    fun tapped(categoria: SitoCategoria) {
        val sites = this.fetchWebsites(categoria)

        if (sites.isEmpty()) {
            when(categoria) {
                SitoCategoria.facebook -> executeQuery("249745821706358", categoria)
                SitoCategoria.instagram -> executeQuery("mgslombardiaemilia", categoria)
                SitoCategoria.youtube -> executeQuery("mgslombardiaemilia", categoria)
                else -> executeQuery("mgslombardiaemilia", categoria)
            }
        } else if (sites.size == 1) {
            executeQuery(sites.first().profileName, categoria)
        } else {
            val items : Array<CharSequence> = sites.map { it.name }.toTypedArray()


            val builder = AlertDialog.Builder(this)
            builder.setTitle("Diverse opzioni")
            builder.setMessage("Segui diverse province/città, quindi scegli quale profilo vorresti raggiungere")
            builder.setItems(items) { dialog, which ->
                Toast.makeText(applicationContext, items[which], Toast.LENGTH_LONG).show()
                //this.executeQuery(sites[which].profileName, categoria)
            }
            builder.show()
        }
    }


    fun executeQuery(username: String?, categoria: SitoCategoria) {
        if (username == null) {
            println("BAH, username == null in executeQuery")
            return
        }

        var query = ""
        var baseUrl = ""

        when(categoria) {
            SitoCategoria.facebook -> {
                query = "fb://profile/$username/wall"
                baseUrl = "https://www.facebook.com/$username"
            }
            SitoCategoria.instagram -> {
                query = "https://instagram.com/_u/$username"
                baseUrl = "https://instagram.com/$username"
            }
            SitoCategoria.youtube -> {
                query = "vnd.youtube://user/channel/$username"
                baseUrl = "https://www.youtube.com/user/$username"
            }
            else -> {}
        }

        try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(query))
            this@SocialActivity.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl))
            this@SocialActivity.startActivity(webIntent)
        }

    }
}