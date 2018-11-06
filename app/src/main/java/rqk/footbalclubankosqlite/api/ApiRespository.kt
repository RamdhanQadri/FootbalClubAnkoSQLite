package rqk.footbalclubankosqlite.api

import java.net.URL

class ApiRespository {
    fun doRequest(url: String): String {
        return URL(url).readText()
    }
}