package com.danitox.igio_android

import android.content.Context
import android.view.View
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.util.*

open class RegolaVita: RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    var dateOfModification: Date = Date()
    private var _scuolaType: Int = -1
    var categorie: RealmList<RegolaCategoria> = RealmList()

    var scuolaType: ScuolaType
        get() { return ScuolaType.none.getFrom(_scuolaType) }
        set(value) { _scuolaType = value.value }

    class RiassuntoDomandaIndex(var categoriaIndex: Int, var domandaIndex: Int)
    val domandeRiassuntoRaw: List<RiassuntoDomandaIndex>
        get() {
            return listOf(
                RiassuntoDomandaIndex(0, 4),
                RiassuntoDomandaIndex( 1, 9),
                RiassuntoDomandaIndex( 2, 12),
                RiassuntoDomandaIndex( 3, 15),
                RiassuntoDomandaIndex( 4, 18),
                RiassuntoDomandaIndex( 5, 21)
            )
        }

    val domandeRiassunto: List<RegolaDomanda>
        get() {
            val realm = Realm.getDefaultInstance()
            val allObjects : MutableList<RegolaDomanda> = mutableListOf()
            for (domandaIndex in this.domandeRiassuntoRaw) {
                val filteredObjects = realm.where(RegolaDomanda::class.java).equalTo("order", domandaIndex.domandaIndex).equalTo("categoria.regola._scuolaType", ScuolaType.triennio.value).findAll()
                allObjects.addAll(filteredObjects)
            }
            return allObjects
        }
}

open class RegolaCategoria: RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    var order: Int = 0
    var nome : String = ""

    var domande : RealmList<RegolaDomanda> = RealmList()
    @LinkingObjects("categorie") val regola: RealmResults<RegolaVita>? = null
}

open class RegolaDomanda: RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    var order: Int = 0
    var domanda: String = ""
    var risposta: String? = null
    @LinkingObjects("domande") val categoria : RealmResults<RegolaCategoria>? = null
}

class RegolaFile {

    @SerializedName("scuolaType") private var _scuolaType : Int = 0
    var scuolaType: ScuolaType
        get() { return ScuolaType.none.getFrom(_scuolaType) }
        set(value) { _scuolaType = value.value }

    var categories: MutableList<RegolaCategoriaFile> = mutableListOf()
}

class RegolaCategoriaFile {
    var id: Int = 0
    var name : String = ""
    var domande: MutableList<RegolaDomandaFile> = mutableListOf()
}

class RegolaDomandaFile {
    var idDomanda: Int = 0
    var domanda: String = ""
    var risposta: String? = null
}

class RegolaHelper {
    fun createFromFile(regolaFile: RegolaFile): RegolaVita {
        val regolaCD = RegolaVita()
        regolaCD.scuolaType = regolaFile.scuolaType

        for (categoriaFile in regolaFile.categories) {
            val categoriaCD = RegolaCategoria()
            regolaCD.categorie.add(categoriaCD)
            categoriaCD.nome = categoriaFile.name
            categoriaCD.order = categoriaFile.id

            for (domandaFile in categoriaFile.domande) {
                val domandaCD = RegolaDomanda()
                categoriaCD.domande.add(domandaCD)
                domandaCD.domanda = domandaFile.domanda
                domandaCD.risposta = domandaFile.risposta
                domandaCD.order = domandaFile.idDomanda
            }
        }
        return regolaCD
    }
}

class RegolaFetcherModel(val context: Context) {

    enum class RegolaFileNames(val value: String) {
        medie("regolaMedie.json"),
        biennio("regolaBiennio.json"),
        triennio("regolaTriennio.json")
    }

    fun createIfNotPresent() {
        val realm = Realm.getDefaultInstance()
        for (type in ScuolaType.values()) {
            if (type == ScuolaType.none) { continue }

            val allRegole = realm.where(RegolaVita::class.java).equalTo("_scuolaType", type.value).findAll()
            if (allRegole.size < 1) {
                val fileName : RegolaFileNames = when (type) {
                    ScuolaType.medie ->  RegolaFileNames.medie
                    ScuolaType.biennio -> RegolaFileNames.biennio
                    ScuolaType.triennio -> RegolaFileNames.triennio
                    else -> RegolaFileNames.medie
                }
                createRegolaModel(fileName)
            }
        }
    }

    fun createRegolaModel(fileName: RegolaFileNames) {
        val stream = this.context.assets.open(fileName.value)
        val rawBytes = stream.readBytes()
        stream.close()
        val jsonString = String(rawBytes)
        val gson = GsonBuilder().create()
        val regolaFile = gson.fromJson(jsonString, RegolaFile::class.java)

        val newRegola = RegolaHelper().createFromFile(regolaFile)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insert(newRegola)
        realm.commitTransaction()
    }

    fun getLatestRegola(type: ScuolaType) : RegolaVita? {
        val realm = Realm.getDefaultInstance()
        return realm.where(RegolaVita::class.java).equalTo("_scuolaType", type.value).findFirst()
    }

}