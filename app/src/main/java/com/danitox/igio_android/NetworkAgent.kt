package com.danitox.igio_android

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import okhttp3.*
import java.io.IOException
import java.io.Serializable

class NetworkAgent<Response>(val classType: Class<Response>) {

    //TODO: create un public enum con tutti i link (es: mainUrl) per facilitare la modifica in futuro

    fun executeNetworkRequest(request: ToxNetworkRequest, responseCompletion: (Response?, String?) -> Unit) {
        val pathComponent = request.requestType.value
        var urlString = "http://192.168.1.5/iGio-Server/".plus(pathComponent).plus(".php")

        if (request.args != null) {
            val args = request.args!!
            urlString = this.getFullPath(urlString, args)
        }

        val httpRequest = Request.Builder().url(urlString).build()

        val client = OkHttpClient()
        client.newCall(httpRequest).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                responseCompletion(null, "Un cribio di errore nella richiesta")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()

                try {
                    val response = gson.fromJson(body, classType!!)
                    responseCompletion(response, null)
                } catch (exc: JsonParseException) {
                    responseCompletion(null, exc.localizedMessage.plus("\nRAW_RESPONSE: $body"))
                }

            }

        })

    }

    fun getFullPath(url: String, args: Map<String, String>) : String {
        var urlString = url.plus("?")

        for ((key, value) in args) {
            if (key.isBlank() || value.isBlank()) { continue }
            urlString = urlString.plus("$key=$value&")
        }

        if (urlString.endsWith("&")) {
            urlString = urlString.removeSuffix("&")
        }
        if (urlString.endsWith("?")) {
            urlString = urlString.removeSuffix("?")
        }

        return urlString
    }

}

enum class RequestType(val value: String) {
    none(""),
    preghiere("preghiere"),
    materiali("materiali"),
    locations("locations"),
    diocesi("diocesi"),
    cities("cities"),
    localizedSites("resources")
}

class ToxNetworkRequest {
    var requestType: RequestType = RequestType.none
    var args: Map<String, String>? = null
}