package com.danitox.igio_android

import com.google.gson.reflect.TypeToken
import io.realm.Realm

class SitiLocalizer {

    var errorHandler: ((String) -> Unit)? = null

    fun fetchLocalizedWebsites(location: LocationCodable? = null, saveRecords: Boolean = true, completionHandler: (LocalizedList?, String?) -> Unit) {
        val request = ToxNetworkRequest()
        request.requestType = RequestType.localizedSites

        if (location != null) {
            request.args = mapOf(Pair("locationID", location.id.toString()))
        }

        this.fetchFromServer(saveRecords, request) { list, error ->
            completionHandler(list, error)
        }
    }

    fun fetchFromServer(saveRecords: Boolean, req: ToxNetworkRequest, completionHandler: (LocalizedList?, String?) -> Unit) {
        val agent = NetworkAgent(LocalizedList::class.java)
        agent.executeNetworkRequest(req) { list, error ->
            if (error == null && list != null) { //Success!
                if (saveRecords) { saveLocalizedSitesList(list) }
                completionHandler(list, null)
            }
            else {
                completionHandler(null, error)
            }
        }

    }

    private fun saveLocalizedSitesList(list: LocalizedList) {
        val realm = Realm.getDefaultInstance()
        val allSites = list.siti
        realm.beginTransaction()

        for (codableSite in allSites) {
            val savedSite = realm.where(SitoWeb::class.java).equalTo("id", codableSite.id).findFirst()
            if (savedSite != null) {
                savedSite.updateContents(codableSite)
            } else {
                val creationHelper = SitoWebHelper()
                val newSite = creationHelper.createSitoFromCodable(codableSite)
                realm.insert(newSite)
            }
        }

        realm.commitTransaction()
    }

    fun fetchLocalWebsites(type: SitoCategoria) : List<SitoObject> {
        val realm = Realm.getDefaultInstance()
        return realm.where(SitoWeb::class.java).equalTo("_categoria", type.value).findAll().map { SitoWebHelper().createCodableFromSito(it) }
    }

    fun getLocations(type: LocationType, saveRecords: Boolean = true, completionHandler: ((List<LocationCodable>?, String?) -> Unit)? = null) {
        val request = ToxNetworkRequest()
        request.requestType = RequestType.locations
        request.args = mapOf(Pair("type", type.value.toString()))

        //val agent = NetworkAgent(mutableListOf<LocationCodable>()::class.java)
        val agent = NetworkAgent(listOf<LocationCodable>()::class.java, object : TypeToken<List<LocationCodable>>(){}.type)
        agent.executeNetworkRequest(request) { list, error ->
            if (error == null && list != null) {
                if (saveRecords) {
                    this.saveLocations(list)
                }
                completionHandler!!(list, null)
            } else {
                completionHandler!!(null, error)
            }
        }
    }

    fun saveLocations(locations: List<LocationCodable>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        for (codable in locations) {
            val savedObject = realm.where(Location::class.java).equalTo("id", codable.id).findFirst()
            if (savedObject != null) {
                savedObject.name = codable.name
            } else {
                val newObject = SitoWebHelper().createLocationFromCodable(codable)
                realm.insert(newObject)
            }
        }

        val currentSavedIDs = realm.where(Location::class.java).findAll().map { it.id }
        val newIDs = locations.map { it.id }

        val filtered = newIDs.filter { !currentSavedIDs.contains(it) }

        val objectsToRemove = filtered.mapNotNull { realm.where(Location::class.java).equalTo("id", it).findFirst() }
        objectsToRemove.forEach { it.deleteFromRealm() }

        realm.commitTransaction()
    }

    fun updateFromLocal(locations: List<LocationCodable>) : MutableList<LocationCodable> {
        val localRealm = Realm.getDefaultInstance()
        val allLocations : MutableList<LocationCodable> = mutableListOf()

        locations.forEach {
            val savedObj = localRealm.where(Location::class.java).equalTo("id", it.id).findFirst()
            val newObj = it
            newObj.isSelected = if (savedObj == null)  false else savedObj.isSelected
            allLocations.add(newObj)
        }

        return allLocations
    }

    fun fetchLocalLocations(type: LocationType) : MutableList<LocationCodable> {
        val realm = Realm.getDefaultInstance()
        val objects = realm.where(Location::class.java).equalTo("_type", type.value).findAll().map { it }
        var allLocationsCodable : MutableList<LocationCodable> = mutableListOf()

        objects.forEach {
            val newCodable = LocationCodable()
            newCodable.id = it.id
            newCodable.name = it.name
            newCodable.loctype = it.type
            newCodable.isSelected = it.isSelected
            allLocationsCodable.add(newCodable)
        }

        return allLocationsCodable
    }

    fun removeSites(obj: LocationCodable) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val objects = realm.where(SitoWeb::class.java).equalTo("location.id", obj.id).findAll()

        objects.forEach { it.deleteFromRealm() }

        realm.commitTransaction()
    }

    fun toggle(location: LocationCodable) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val savedLocation = realm.where(Location::class.java).equalTo("id", location.id).findFirst()
        if (savedLocation != null) {
            savedLocation.isSelected = savedLocation.isSelected.not()
        }
        realm.commitTransaction()
    }

    fun getAllLocalSelectedLocations() : List<LocationCodable> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Location::class.java).equalTo("isSelected", true).findAll().map { SitoWebHelper().createCodableLocationFrom(it) }
    }

    fun fetchAllWebsites(saveRecords: Boolean = true, completionHandler: (LocalizedList?, String?) -> Unit) {
        val locations = this.getAllLocalSelectedLocations()

        var allSites: MutableSet<SitoObject> = mutableSetOf()
        var locerror: String? = null

        val group = DispatchGroup()

        if (locations.isEmpty()) {
            group.enter()
            this.fetchLocalizedWebsites { list, error ->
                if (error == null && list != null) {
                    allSites.addAll(list.siti)
                } else {
                    locerror = error
                }
                group.leave()
            }
        } else {
            for (location in locations) {
                group.enter()
                this.fetchLocalizedWebsites(location) { list, error ->
                    if (error == null && list != null) {
                        allSites.addAll(list.siti)
                    } else {
                        locerror = error
                    }
                    group.leave()
                }
            }
        }

        group.notify {
            if (locerror != null) {
                completionHandler(null, locerror)
            } else {
                val list = LocalizedList()
                val uniqueArray : MutableList<SitoObject> = mutableListOf()
                val completeList = allSites.sortedBy { it.type.value }
                for (sito in completeList) {
                    if (uniqueArray.firstOrNull { it.id == sito.id } == null) {
                        uniqueArray.add(sito)
                    }
                }
                list.siti = uniqueArray
                completionHandler(list, null)
            }
        }

    }

}