package com.danitox.igio_android

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.widget.Toolbar
import io.realm.Realm
import kotlinx.android.synthetic.main.note_editor.*

class NoteEditorActivity : AppCompatActivity() {

    var note: Note? = null
    private var realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteID = intent.getStringExtra("noteID")
        this.note = realm.where(Note::class.java).equalTo("id", noteID).findFirst()

        setContentView(R.layout.note_editor)

        this.editorView.text = Editable.Factory.getInstance().newEditable(this.note?.body)


    }

    override fun onPause() {
        super.onPause()
        println("onPause!")
        saveNote()
    }

    private fun saveNote() {
        if (this.editorView.text.isBlank()) {
            this.removeNote()
            return
        }

        val title = this.editorView.text.lines().first()
        realm.beginTransaction()
        note?.title = title
        note?.body = this.editorView.text.toString()
        realm.insertOrUpdate(note)
        realm.commitTransaction()
    }

    private fun removeNote() {
        realm.beginTransaction()
        note?.deleteFromRealm()
        realm.commitTransaction()
    }

}