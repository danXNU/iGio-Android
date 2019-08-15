package com.danitox.igio_android

import android.content.Context
import android.content.res.AssetManager
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
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

    fun createCompagniaModel(fileName: VerificaFileNames) {
        this.context.assets.open("")
        //TODO: da implementare
    }
}

class VerificaCompagnia: RealmObject() {
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var _scuolaType : Int = 0
    var categorie: RealmList<VerificaCategoria> = RealmList()

    var scuolaType: ScuolaType
        get() { return ScuolaType.none.getFrom(_scuolaType) }
        set(value) { _scuolaType = value.value }


}


class VerificaCategoria: RealmObject() {
    var name: String = ""
    @LinkingObjects("categorie") val verifica: RealmResults<VerificaCompagnia>? = null
    var domande : RealmList<VerificaDomanda> = RealmList()
}

class VerificaDomanda: RealmObject() {
    var domanda: String = ""
    var risposta: Int = 0
    @LinkingObjects("domande") val categoria : RealmResults<VerificaCategoria>? = null
}