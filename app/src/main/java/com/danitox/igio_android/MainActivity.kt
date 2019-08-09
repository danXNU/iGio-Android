package com.danitox.igio_android

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val listView = findViewById<ListView>(R.id.main_listView)
        listView.adapter = listAdapter

        listAdapter.clickAction = { noteID ->
            val intent = Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("noteID", noteID)
            listView.context.startActivity(intent)
        }

        listAdapter.agent.errorHandler = { error ->
            Log.e("Tox", "Il NoteAgent ha ricevuto un errore ${error.localizedMessage}")
        }

        listAdapter.dataLoaded = {
            Log.e("Tox", "Note fetchate!")
            listAdapter.notifyDataSetChanged()
        }

        listAdapter.updateData()

        this.add_button.setOnClickListener {
            val newNote = this.listAdapter.getNewNote()

            val intent = Intent(this, NoteEditorActivity::class.java)
            intent.putExtra("noteID", newNote.id)
            this.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        
        listAdapter.updateData()
    }

    private class NotesAdapter: BaseAdapter() {

        val agent: NotesAgent = NotesAgent()
        var dataLoaded: (() -> Unit)? = null
        var clickAction: ((String) -> Unit)? = null

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

        fun remove(note: Note) {
            agent.remove(note)
        }

        override fun getCount(): Int {
            return this.agent.allNotes.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val row: View

            if (convertView == null) {
                val inflater = LayoutInflater.from(parent!!.context)
                row = inflater.inflate(R.layout.note_row, parent, false)

                row.tag = ViewHolder(row.mainLabel, row.paroleCountLabel)
            } else {
                row = convertView
            }


            val note = this.agent.allNotes[position]
            val holder = row.tag as ViewHolder

            holder.mainLabel.text = note.title
            holder.countLabel.text = "${note.body.length} lettere"

            row.setOnClickListener {
                clickAction?.invoke(note.id)
            }

            return row
        }

        private class ViewHolder(val mainLabel: TextView, val countLabel: TextView)

    }

}
