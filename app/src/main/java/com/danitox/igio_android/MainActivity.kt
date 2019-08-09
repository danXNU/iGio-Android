package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.note_row.view.*

class MainActivity : AppCompatActivity() {

    private val listAdapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = listAdapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        tableView.addItemDecoration(divider)

        listAdapter.clickAction = { noteID ->
            val intent = Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("noteID", noteID)
            this.startActivity(intent)
        }

        listAdapter.agent.errorHandler = { error ->
            Log.e("Tox", "Il NoteAgent ha ricevuto un errore ${error.localizedMessage}")
        }

        listAdapter.dataLoaded = {
            Log.e("Tox", "Note fetchate!")
            listAdapter.notifyDataSetChanged()
        }

        this.add_button.setOnClickListener {
            val newNote = this.listAdapter.getNewNote()

            val intent = Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("noteID", newNote.id)
            this.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        println("onResume!")
        listAdapter.updateData()
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 121) {
            this.listAdapter.remove(item.groupId)
            showRemoveMessage()
            return true
        } else {
            return super.onContextItemSelected(item)
        }

    }

    private fun showRemoveMessage() {
        Snackbar.make(this.tableView, "Nota rimossa con successo!", Snackbar.LENGTH_SHORT).show()
    }
}

class NotesViewHolder(val view: View, var noteID: String? = null, var clickAction: ((String) -> Unit)? = null): RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

    init {
        view.setOnClickListener {
            clickAction?.invoke(noteID!!)
        }
        view.noteRow.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(this.adapterPosition, 121, 0, "Rimuovi")

    }

}


private class NotesAdapter: RecyclerView.Adapter<NotesViewHolder>() {

    val agent: NotesAgent = NotesAgent()
    var dataLoaded: (() -> Unit)? = null
    var clickAction: ((String) -> Unit)? = null

    init {
        println("NotesAdapter inizializzato. Spero sia solo questa volta :)")
    }

    fun updateData() {
        agent.fullFetch()
        dataLoaded?.invoke()
    }

    fun getNewNote() : Note {
        return agent.createNewNote()
    }

    fun getTestNote() : Note {
        return agent.createTestNote()
    }

    fun remove(note: Note, index: Int) {
        agent.remove(note)
        this.notifyItemRemoved(index)
    }

    fun remove(atIndex: Int) {
        val note = agent.allNotes[atIndex]
        this.remove(note, atIndex)
    }

    override fun getItemCount(): Int {
        println("${agent.allNotes.size} notes\n")
        return agent.allNotes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.note_row, parent, false)
        return NotesViewHolder(cell)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = agent.allNotes[position]
        holder.view.mainLabel.text = note.title
        holder.view.paroleCountLabel.text = note.getBodyWordsCount().toString().plus(" parole")
        holder.noteID = note.id
        holder.clickAction = this.clickAction
    }

}
