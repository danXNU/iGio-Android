package com.danitox.igio_android

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.io.File
import java.util.*

class CompagniaAgent(val context: Context) {

    enum class VerificaFileNames(val value: String) {
        medie("verificaMedie.json"),
        biennio("verificaBiennio.json"),
        triennio("verificaTriennio.json")
    }

    fun getFileName(type: ScuolaType) : String {
        return when(type) {
            ScuolaType.medie -> "verificaMedie"
            ScuolaType.biennio -> "verificaBiennio"
            ScuolaType.triennio -> "verificaTriennio"
           else -> ""
        }
    }

    fun getRisposteFile(type: ScuolaType, context: Context) : File {
        val fileName = "risposte_${this.getFileName(type)}.json"
        val folder = File(context.filesDir, "Verifica")
        return File(folder, fileName)
    }

    fun convertRealmToJSON() {
        val realm = Realm.getDefaultInstance()

        val appFolder = context.filesDir
        val verificaFolder = File(appFolder, "Verifica")
        verificaFolder.mkdir()

        for (type in ScuolaType.values()) {
            val allRegole = realm.where(VerificaCompagnia::class.java).equalTo(
                "_scuolaType",
                type.value
            ).findAll()
            if (allRegole.isEmpty()) { continue }
            val regola = allRegole.first() ?: continue

            val domandeFile = CompagniaDomandeFile().get(type, context)
            val risposteFile = CompagniaRisposteFile()


            domandeFile.categorie.forEachIndexed { indexCat, cat ->
                cat.domande.forEachIndexed { indexDom, _ ->
                    val domandaID = domandeFile.categorie[indexCat].domande[indexDom].id

                    risposteFile.risposte[domandaID] = regola.categorie[indexCat]?.domande?.get(
                        indexDom
                    )?.risposta ?: 0
                }
            }

            risposteFile.save(type, context)
        }

    }

    fun getLatestVerifica(type: ScuolaType) : VerificaCompagnia? {
        val realm = Realm.getDefaultInstance()
        val verifica = realm.where(VerificaCompagnia::class.java).equalTo("_scuolaType", type.value).findFirst()
        return verifica
    }

    fun removeAll() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.delete(VerificaCompagnia::class.java)
        realm.commitTransaction()
    }

}

class CompagniaDomandeFile {
    var schoolType: ScuolaType = ScuolaType.medie
    var categorie: List<CompagniaCategoriaFile> = listOf()


    fun get(type: ScuolaType, context: Context) : CompagniaDomandeFile {
        val agent = CompagniaAgent(context)
        val fileName = "${agent.getFileName(type)}.json"
        val stream = context.assets.open(fileName)
        val data = String(stream.readBytes())
        stream.close()

        val gson = GsonBuilder().create()
        return gson.fromJson(data, CompagniaDomandeFile::class.java)
    }


    class CompagniaCategoriaFile {
        var id: UUID = UUID.randomUUID()
        var name: String = ""
        var domande: List<CompagniaDomanda> = mutableListOf()
    }

    class CompagniaDomanda {
        var id = UUID.randomUUID()
        var str: String = ""
    }

}

class CompagniaRisposteFile {
    var risposte: MutableMap<UUID, Int> = mutableMapOf()

    fun save(type: ScuolaType, context: Context) {
        val gson = GsonBuilder().create()
        val data = gson.toJson(this)

        val agent = CompagniaAgent(context)
        val file = agent.getRisposteFile(type, context)
        file.writeText(data, Charsets.UTF_8)
    }

    companion object {
        fun get(type: ScuolaType, context: Context) : CompagniaRisposteFile {
            val agent = CompagniaAgent(context)
            val rawFile = agent.getRisposteFile(type, context)
            if (rawFile.exists() == false) { rawFile.createNewFile() }
            val data = rawFile.readText(Charsets.UTF_8).toString()

            val gson = GsonBuilder().create()
            try {
                val obj = gson.fromJson(data, CompagniaRisposteFile::class.java)
                if (obj == null) {
                    return CompagniaRisposteFile()
                }
                return obj
            } catch (exc: JsonSyntaxException) {
                return CompagniaRisposteFile()
            }

        }
    }
}

open class VerificaCompagnia: RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var _scuolaType : Int = 0
    var categorie: RealmList<VerificaCategoria> = RealmList()

    var scuolaType: ScuolaType
        get() { return ScuolaType.none.getFrom(_scuolaType) }
        set(value) { _scuolaType = value.value }


}


open class VerificaCategoria: RealmObject() {
    var name: String = ""
    @LinkingObjects("categorie") val verifica: RealmResults<VerificaCompagnia>? = null
    var domande : RealmList<VerificaDomanda> = RealmList()
}

open class VerificaDomanda: RealmObject() {
    var domanda: String = ""
    var risposta: Int = 0
    @LinkingObjects("domande") val categoria : RealmResults<VerificaCategoria>? = null
}

/*
class CompagniaFile {

    @SerializedName("scuolaType") private var _scuolaType: Int = 0
    var categorie: MutableList<CompagniaCategoriaFile> = mutableListOf()

    var scuolaType: ScuolaType
        get() {
            return ScuolaType.none.getFrom(_scuolaType)
        }
        set(value) { _scuolaType = value.value }
}

class VerificaCreator {
    fun createFrom(file: CompagniaFile) :  VerificaCompagnia {
        val verifica = VerificaCompagnia()
        verifica.scuolaType = file.scuolaType

        for (categoria in file.categorie) {
            val cdCategoria = VerificaCategoria()
            cdCategoria.name = categoria.nome

            for (domandaString in categoria.domande) {
                val cdDomanda = VerificaDomanda()
                cdDomanda.domanda = domandaString

                cdCategoria.domande.add(cdDomanda)
            }

            verifica.categorie.add(cdCategoria)
        }
        return verifica
    }

}*/