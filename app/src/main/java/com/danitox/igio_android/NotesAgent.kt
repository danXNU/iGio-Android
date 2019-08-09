package com.danitox.igio_android

import android.provider.ContactsContract
import android.renderscript.ScriptGroup
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import java.text.AttributedString
import java.util.*
import kotlin.collections.ArrayList

open class Note : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var date : Date = Date()
    var title: String = ""
    var body: String = ""

    fun getBodyWordsCount() : Int {
        return body.replace("\n", " ").split(" ").size
    }
}

class NotesAgent() {

    var errorHandler: ((Error) -> Unit)? = null
    private var realm: Realm = Realm.getDefaultInstance()

    var allNotes: MutableList<Note> = mutableListOf()

    fun createNewNote() : Note {
        realm.beginTransaction()
        val newNote = Note()
        newNote.date = Date()
        realm.insertOrUpdate(newNote)
        realm.commitTransaction()
        return newNote
    }

    fun createTestNote() : Note {
        realm.beginTransaction()
        val newNote = Note()
        newNote.date = Date()
        newNote.title = "Titolo di prova"
        newNote.body = "Questo è un corpo di prova ed è anche la mia prima note salvata sul database di Realm su Android!"
        realm.insertOrUpdate(newNote)
        realm.commitTransaction()
        return newNote
    }

    fun fullFetch() {
        this.allNotes = realm.where(Note::class.java).findAll().toMutableList()
    }

    fun remove(note: Note) {
        if (this.allNotes.contains(note)) {
            this.allNotes.remove(note)
        }
        realm.beginTransaction()
        note.deleteFromRealm()
        realm.commitTransaction()
    }

}