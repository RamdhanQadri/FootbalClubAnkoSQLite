package rqk.footbalclubankosqlite.presenter

import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rqk.footbalclubankosqlite.api.ApiRespository
import rqk.footbalclubankosqlite.api.TheSportDBApi
import rqk.footbalclubankosqlite.interfac.TeamsView
import rqk.footbalclubankosqlite.model.TeamResponse

class TeamsPresenter(
    private val view: TeamsView,
    private val apiRespository: ApiRespository,
    private val gson: Gson
) {
    fun getTeamList(league: String?) {
        view.showLoading()
        doAsync {
            val data = gson.fromJson(
                apiRespository
                    .doRequest(TheSportDBApi.getTeams(league)),
                TeamResponse::class.java
            )

            uiThread {
                view.hideLoading()
                view.showTeamList(data.teams)
            }
        }
    }
}