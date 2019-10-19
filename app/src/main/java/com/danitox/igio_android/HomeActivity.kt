package com.danitox.igio_android

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.UserManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_cell.view.*

class HomeItem(val id: Int, val name: String, val imageID: Int, val color: Int, val allowedAge: Array<ScuolaType>, val allowedGenders: Array<UserGender>)

class HomeItemsHelper {
    var allItems: List<HomeItem> = listOf(
        HomeItem(0, "Diario Personale", R.drawable.diary, Color.BLUE, ScuolaType.values(), UserGender.values()),
        HomeItem(1, "TeenSTAR", R.drawable.star, Color.MAGENTA, ScuolaType.values(), UserGender.values()),
        HomeItem(2, "GioProNet", R.drawable.weightscale, Color.RED, ScuolaType.values(), UserGender.values()),
        HomeItem(3, "Agenda dell'allegria e della santità", R.drawable.airplane, Color.GREEN, arrayOf(ScuolaType.medie), UserGender.values()),
        HomeItem(4, "Il mio percorso formativo", R.drawable.search, Color.rgb(252,117,40), arrayOf(ScuolaType.biennio, ScuolaType.triennio), UserGender.values()),
        HomeItem(5, "Il progetto delle 3S", R.drawable.airplane, Color.GREEN, arrayOf(ScuolaType.biennio), UserGender.values()),
        HomeItem(6, "Regola di Vita", R.drawable.airplane, Color.GREEN, arrayOf(ScuolaType.triennio), UserGender.values())
    )
}


class HomeActivity : Fragment() {

    val helper = HomeItemsHelper()
    var items: List<HomeItem> = listOf()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tsm_list)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_view, container, false)
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    fun update() {
        val user = UserManager().currentUser()
        items = helper.allItems.filter { it.allowedAge.contains(user.ageScuola) && it.allowedGenders.contains(user.gender) }
        fillTableView()
    }

    fun fillTableView() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.spanCount = 2

        for (item in items) {
            val newHomeItemCell = HomeCell(item) {
                when(item.id) {
                    0 -> { showNoteListController() }
                    1 -> { showTeenStarController() }
                    2 -> { showGioProNetController() }
                    3 -> { showRegolaController() }
                    4 -> { showVerificaCompagniaController() }
                    5 -> { showRegolaController() }
                    6 -> { showRegolaController() }
                    else -> {  }
                }
            }

            adapter.add(newHomeItemCell)

        }

        this.tableView.layoutManager = GridLayoutManager(this.context, adapter.spanCount).apply { spanSizeLookup = adapter.spanSizeLookup }
        this.tableView.adapter = adapter
    }

    fun showNoteListController() {
        val intent = Intent(this.context, NoteListActivity::class.java)
        this.startActivity(intent)
    }

    fun showTeenStarController() {
        val user = UserManager().currentUser()
        if (user.gender == UserGender.boy) {
            val intent = Intent(this.context, TeenStarMaschioListActivity::class.java)
            this.startActivity(intent)
        } else if (user.gender == UserGender.girl) {
            if (user.ageScuola == ScuolaType.medie) {
                val intent = Intent(this.context, TeenStarMaschioListActivity::class.java)
                this.startActivity(intent)
            } else {
                val intent = Intent(this.context, TeenStarFemminaListActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    fun showGioProNetController() {
        val intent = Intent(this.context, GioProListActivity::class.java)
        this.startActivity(intent)
    }

    fun showRegolaController() {
        val model = RegolaFetcherModel(this.context!!)
        model.createIfNotPresent()

        val intent = Intent(this.context, RegolaCategorieActivity::class.java)
        intent.putExtra("type", UserManager().currentUser().ageScuola.value)
        this.startActivity(intent)
    }

    fun showVerificaCompagniaController() {
        val intent = Intent(this.context, CompagniaActivity::class.java)
        intent.putExtra("type", UserManager().currentUser().ageScuola.value)
        this.startActivity(intent)
    }

}

class HomeCell(val homeItem: HomeItem, val clickAction: (HomeItem) -> Unit): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            clickAction.invoke(homeItem)
        }

        viewHolder.itemView.setBackgroundColor(homeItem.color)
        viewHolder.itemView.itemTitleLabel.text = homeItem.name
        viewHolder.itemView.iconView.setBackgroundResource(homeItem.imageID)
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 2
    }

    override fun getLayout(): Int {
        return R.layout.home_cell
    }
}