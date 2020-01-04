package com.danitox.igio_android

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.lang.reflect.Type

class NetworkAgent<Response>(val classType: Class<Response>, val typedValue: Type? = null) {

    fun executeNetworkRequest(request: ToxNetworkRequest, responseCompletion: (Response?, String?) -> Unit) {
        val pathComponent = request.requestType.value
        var urlString = "${URLs.mainURL.rawValue}/".plus(pathComponent).plus(".php")

        if (request.args != null) {
            val args = request.args!!
            urlString = this.getFullPath(urlString, args)
        }

        val httpRequest = Request.Builder().url(urlString).build()

        val client = OkHttpClient()
        client.newCall(httpRequest).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                responseCompletion(null, "Errore nella richiesta: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()

                try {
                    val response: Response
                    if (typedValue == null) {
                        response = gson.fromJson(body, classType)
                    } else {
                        response = gson.fromJson(body, typedValue)
                    }

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

enum class URLs(val rawValue: String) {
    //mainURL("http://192.168.1.5/iGio-Server"),
    //mainURL("http://danitoxnu.ddns.net:1610/iGio-Server"),
    mainURL("http://igioapp.altervista.org/iGio-Server"),
    calendarioURL("https://calendar.google.com/calendar/embed?src=calendario@salesiani-ile.org&color=%23A32929&src=salesiani-ile.org_m8ep8fvvgg6tcqeioe9mkressg@group.calendar.google.com&color=%23B1365F&src=salesiani-ile.org_u5969o21d5mi0tudjomlq9hnvo@group.calendar.google.com&color=%237A367A&src=salesiani-ile.org_pql8ca5t2b23uoac4nvrt4g9t4@group.calendar.google.com&color=%235229A3&src=salesiani-ile.org_dn1qg5qkb359s7l4ts7427skcc@group.calendar.google.com&color=%2329527A&src=salesiani-ile.org_213b6s5kr725fmp9nhlo53hq58@group.calendar.google.com&color=%232952A3&src=salesiani-ile.org_bhbsjh07ve118qn4mqsr0eup18@group.calendar.google.com&color=%231B887A&src=salesiani-ile.org_c00t56e41b3sfhoreba9ju9754@group.calendar.google.com&color=%2328754E&src=salesiani-ile.org_90kchlu01jjnfjdbl2ns507mvs@group.calendar.google.com&color=%230D7813&src=salesiani-ile.org_81h3mpiecdmv29569ug3gq8i9k@group.calendar.google.com&color=%23528800&src=salesiani-ile.org_u54tn0tntab9ddip4lql09g068@group.calendar.google.com&color=%2388880E&src=salesiani-ile.org_aj6nqimgm8hhloai0el4slhe8s@group.calendar.google.com&color=%23AB8B00&src=salesiani-ile.org_n27im05dnubqi6499nlkijpl4s@group.calendar.google.com&color=%23BE6D00&src=salesiani-ile.org_h8b9ner11dc59ht9oglkac4vfc@group.calendar.google.com&color=%23B1440E&src=salesiani-ile.org_ep320295u02hgs4dv5jtqr1tmk@group.calendar.google.com&color=%23865A5A&src=salesiani-ile.org_qs3qetrgph23lmdfincodaa7g0@group.calendar.google.com&color=%23705770&src=salesiani-ile.org_4j2qsptdi7fi30c693rc5kec1k@group.calendar.google.com&color=%234E5D6C&src=salesiani-ile.org_psdg01m8vequlprtn2n2a89rm0@group.calendar.google.com&color=%234E8D6C&src=salesiani-ile.org_aeg9rpolr2d42tjvt3u4vg2mgs@group.calendar.google.com&color=%235A6986&src=salesiani-ile.org_psmofdufsab96thfssbe3rs96c@group.calendar.google.com&color=%234A716C&src=it.christian%23holiday@group.v.calendar.google.com&color=%236E6E41&src=it.italian%23holiday@group.v.calendar.google.com&color=%236E6E41&ctz=Europe/Rome&showTitle=1&showNav=1&showDate=1&showTabs=1&showCalendars=1&hl=it")
}