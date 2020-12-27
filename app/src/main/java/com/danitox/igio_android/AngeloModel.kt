package com.danitox.igio_android

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.io.File
import java.util.*

class AngeloDomandeFile {
    var domande: List<Item> = listOf()
    var parole: List<Item> = listOf()

    class Item {
        var str: String = ""
        var id: UUID = UUID.randomUUID()
    }

    companion object {
        fun get(context: Context): AngeloDomandeFile {
            val file = context.assets.open("angelo_custode.json")
            val data = String(file.readBytes())
            file.close()

            val gson = GsonBuilder().create()
            return gson.fromJson(data, AngeloDomandeFile::class.java)
        }
    }
}

class AngeloRispostaFile {
    var risposte: HashMap<UUID, String> = hashMapOf()
    var paroleChecked: HashMap<UUID, Boolean> = hashMapOf()
    var preghieraParola: String = ""

    companion object {
        fun get(context: Context): AngeloRispostaFile {
            val folder = File(context.filesDir, "Angelo")
            if (folder.exists() == false) { folder.mkdir() }

            val file = File(folder, "risposte_angelo.json")
            if (file.exists() == false) { file.createNewFile() }

            val data = file.readText(Charsets.UTF_8).toString()

            val gson = GsonBuilder().create()
            try {
                val obj = gson.fromJson(data, AngeloRispostaFile::class.java)
                if (obj == null) {
                    return AngeloRispostaFile()
                }
                return obj
            } catch (exc: JsonSyntaxException) {
                return AngeloRispostaFile()
            }

        }
    }

    fun save(context: Context) {
        val gson = GsonBuilder().create()
        val data = gson.toJson(this)

        val folder = File(context.filesDir, "Angelo")
        val file = File(folder, "risposte_angelo.json")

        file.writeText(data)
    }

}