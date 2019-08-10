package com.danitox.igio_android

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.net.URL

open class Location : RealmObject() {
    @PrimaryKey
    var id : Int = 0
    var name :  String = ""
    var isSelected: Boolean = false

    private var _type: Int = 0
    var type: LocationType
        get() {
            if (_type == 1) { return LocationType.diocesi }
            else { return LocationType.city }
        }
        set(value) { _type = value.value }

}

enum class LocationType(val value: Int) {
    diocesi(1),
    city(2)
}


open class SitoWeb : RealmObject() {
    @PrimaryKey
    var id : Int = -1
    var order : Int = -1
    var nome : String = ""
    var descrizione: String = ""
    var urlString : String = ""
    var _scuolaType : Int = 0
    var categoria : Int = 0

    var location: Location? = null

    fun updateContents(codable: SitoObject) {
        this.categoria = codable.type.value
        this.nome = codable.name
        this.descrizione = codable.descrizione
        this.order = codable.order
        this._scuolaType = codable.scuolaType.value
        this.urlString = codable.urlString

        if (codable.locationID != null) {
            this.location = realm.where(Location::class.java).equalTo("id", codable.locationID).findFirst()
        }
    }

}

class SitoObject {
    var id : Int = -1
    var name: String = ""
    var urlString: String = ""

    var type: SitoCategoria = SitoCategoria.none
    var scuolaType: ScuolaType = ScuolaType.none

    var locationID: Int? = null

    var descrizione: String = ""
    var order: Int = -1
}

//TODO: testare se gson riesce a creare un Enum a partire da un valore. Es: enum Location { case dicoesi = 1 }. JSON: { "test" : 1 } --> json["test"] == Location.diocesi
class LocationCodable {
    var id : Int = -1
    var name: String = ""
    var type: LocationType = LocationType.diocesi

    var isSelected: Boolean = false
}

enum class ScuolaType(val value: Int) {
    none(0),
    medie(1),
    biennio(2),
    triennio(3)
}

enum class SitoCategoria(val value: Int){
    none(0),
    materiali(1),
    preghiere(2),
    facebook(3),
    instagram(4),
    youtube(5),
    calendario(6)
}

class LocalizedList {
    var siti: MutableList<SitoObject> = mutableListOf()
}

//Helper utilizzato per bypassare il limite di Kotlin di avere delle funzioni statiche
class SitoWebHelper {
    fun createSitoFromCodable(codable: SitoObject) : SitoWeb {
        val newSite = SitoWeb()
        newSite.id = codable.id

        newSite.updateContents(codable)

        return newSite
    }
}