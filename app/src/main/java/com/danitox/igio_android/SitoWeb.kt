package com.danitox.igio_android

import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import okhttp3.internal.notifyAll
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
    none(0),
    diocesi(1),
    city(2);

    fun getFrom(value: Int) : LocationType {
        val categoria =  when(value) {
            0 -> none
            1 -> diocesi
            2 -> city
            else -> none
        }
        if (categoria == none) { Log.e("Enum error", "LocationType ricevuto == .none") }
        return categoria
    }
}


open class SitoWeb : RealmObject() {
    @PrimaryKey
    var id : Int = -1
    var order : Int = -1
    var nome : String = ""
    var descrizione: String = ""
    var urlString : String = ""
    var _scuolaType : Int = 0
    var _categoria : Int = 0

    var location: Location? = null

    var categoria: SitoCategoria
        get() {
            return SitoCategoria.none.getFrom(_categoria)
        }
        set(value) { _categoria = value.value }

    var scuolaType: ScuolaType
        get() {
            return ScuolaType.none.getFrom(_scuolaType)
        }
        set(value) { _scuolaType = value.value }


    fun updateContents(codable: SitoObject) {
        this.categoria = codable.type
        this.nome = codable.name
        this.descrizione = codable.descrizione
        this.order = codable.order
        this.scuolaType = codable.scuolaType
        this.urlString = codable.urlString

        if (codable.locationID != null) {
            val locRealm = Realm.getDefaultInstance()
            this.location = locRealm.where(Location::class.java).equalTo("id", codable.locationID).findFirst()
        }
    }

}

class SitoObject {
    var id : Int = -1
    var name: String = ""
    var urlString: String = ""

    @SerializedName("type") var _type: Int = 0
    var type: SitoCategoria
        get() {
            return SitoCategoria.none.getFrom(_type)
        }
        set(value) {
            _type = value.value
        }

    @SerializedName("scuolaType") var _scuolaType: Int = 0
    var scuolaType: ScuolaType
        get() {
            return ScuolaType.none.getFrom(_scuolaType)
        }
        set(value) {
            _scuolaType = value.value
        }

    var profileName: String? = null
        get() {
            val availableCategories: List<SitoCategoria> = listOf(SitoCategoria.facebook, SitoCategoria.instagram, SitoCategoria.youtube)
            if (!availableCategories.contains(this.type)) {
                return null
            }
            return this.urlString
        }

    var locationID: Int? = null

    var descrizione: String = ""
    var order: Int = -1
}

class LocationCodable {
    var id : Int = -1
    var name: String = ""
    var type: Int = -1

    var loctype: LocationType
        get() {
            return LocationType.none.getFrom(type)
        }
        set(value) {
            this.type = value.value
        }

    @Expose(deserialize = false) var isSelected: Boolean = false
}


enum class SitoCategoria(val value: Int){
    none(0),
    materiali(1),
    preghiere(2),
    facebook(3),
    instagram(4),
    youtube(5),
    calendario(6);

    fun getFrom(value: Int) : SitoCategoria {
        val categoria =  when(value) {
            1 -> materiali
            2 -> preghiere
            3 -> facebook
            4 -> instagram
            5 -> youtube
            6 -> calendario
            else -> none
        }
        if (categoria == none) { Log.e("Enum error", "SitoCategoria ricevuto == SitoCategoria.none") }
        return categoria
    }
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

    fun createCodableFromSito(obj: SitoWeb) : SitoObject {
        val newCodable = SitoObject()
        newCodable.id = obj.id
        newCodable.name = obj.nome
        newCodable.urlString = obj.urlString
        newCodable.type = obj.categoria
        newCodable.scuolaType = obj.scuolaType
        newCodable.locationID = obj.location?.id
        newCodable.descrizione = obj.descrizione
        newCodable.order = obj.order
        return newCodable
    }


    fun createCodableLocationFrom(obj: Location) : LocationCodable {
        val codable = LocationCodable()
        codable.id = obj.id
        codable.name = obj.name
        codable.loctype = obj.type
        codable.isSelected = obj.isSelected
        return codable
    }

    fun createLocationFromCodable(codable: LocationCodable) : Location {
        val location = Location()
        location.id = codable.id
        location.name = codable.name
        location.type = codable.loctype
        return location
    }
}