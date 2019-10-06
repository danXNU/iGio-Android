package com.danitox.igio_android

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import okhttp3.internal.notifyAll
import java.util.*

class CompagniaAgent(val context: Context) {

    enum class VerificaFileNames(val value: String) {
        medie("verificaMedie.json"),
        biennio("verificaBiennio.json"),
        triennio("verificaTriennio.json")
    }

    fun createIfNotPresent() {
        val realm = Realm.getDefaultInstance()

        for (type in ScuolaType.values()) {
            if (type == ScuolaType.none) { continue }
            val allRegole = realm.where(VerificaCompagnia::class.java).equalTo("_scuolaType", type.value).findAll()
            if (allRegole.size < 1) {
                var fileName : VerificaFileNames = when (type) {
                    ScuolaType.medie ->  VerificaFileNames.medie
                    ScuolaType.biennio -> VerificaFileNames.biennio
                    ScuolaType.triennio -> VerificaFileNames.triennio
                    else -> VerificaFileNames.medie
                }
                createCompagniaModel(fileName)
            }
        }

    }

    private fun createCompagniaModel(fileName: VerificaFileNames) {
        val stream = this.context.assets.open(fileName.value)
        val rawBytes = stream.readBytes()
        stream.close()
        val jsonString = String(rawBytes)
        val gson = GsonBuilder().create()
        val compagniaFile = gson.fromJson(jsonString, CompagniaFile::class.java)

        val newCompagnia = VerificaCreator().createFrom(compagniaFile)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insert(newCompagnia)
        realm.commitTransaction()
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


class CompagniaFile {

    @SerializedName("scuolaType") private var _scuolaType: Int = 0
    var categorie: MutableList<CompagniaCategoriaFile> = mutableListOf()

    var scuolaType: ScuolaType
        get() {
            return ScuolaType.none.getFrom(_scuolaType)
        }
        set(value) { _scuolaType = value.value }
}

class CompagniaCategoriaFile {
    var nome: String = ""
    var domande: MutableList<String> = mutableListOf()
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

}