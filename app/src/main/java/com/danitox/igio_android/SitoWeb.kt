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
