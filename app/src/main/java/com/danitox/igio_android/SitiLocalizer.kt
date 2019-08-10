package com.danitox.igio_android

import io.realm.Realm

class SitiLocalizer {

    val realm = Realm.getDefaultInstance()

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
        val allSites = list.siti
        realm.beginTransaction()

        for (codableSite in allSites) {
            //TODO: da finire di implementare
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

}