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

}

class SitoObject {
    var id : Int = -1
    var name: String = ""
    var urlString: String = ""

    var type: SitoCategoria = SitoCategoria.none
    var scuolaType: ScuolaType = ScuolaType.none

    var locationID: Int? = null
}

enum class ScuolaType(value: Int) {
    none(0),
    medie(1),
    biennio(2),
    triennio(3)
}

enum class SitoCategoria(value: Int){
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