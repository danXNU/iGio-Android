package com.danitox.igio_android

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.tableView
import kotlinx.android.synthetic.main.note_row.view.*

class NoteListActivity : AppCompatActivity() {

    private val listAdapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msgLabel.text = "Nessuna pagina di diario."

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

        this.edit_button.setOnClickListener {
            val newNote = this.listAdapter.getNewNote()

            val intent = Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("noteID", newNote.id)
            this.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        println("onResume!")
        update()
    }

    fun update() {
        listAdapter.updateData()
        if (listAdapter.agent.allNotes.isEmpty()) {
            msgLabel.visibility = VISIBLE
        } else {
            msgLabel.visibility = INVISIBLE
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 121) {
            this.listAdapter.remove(item.groupId)
            showRemoveMessage()
            update()
            return true
        } else {
            return super.onContextItemSelected(item)
        }

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
        holder.view.dateLabel.text = note.getReadbleDate()

        val paroleCount = note.getBodyWordsCount()
        if (paroleCount == 1) {
            holder.view.paroleCountLabel.text = paroleCount.toString().plus(" parola")
        } else {
            holder.view.paroleCountLabel.text = paroleCount.toString().plus(" parole")
        }


        holder.noteID = note.id
        holder.clickAction = this.clickAction
    }

}
