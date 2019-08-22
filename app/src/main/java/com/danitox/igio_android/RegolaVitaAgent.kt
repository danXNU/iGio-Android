package com.danitox.igio_android

import com.google.gson.annotations.SerializedName
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