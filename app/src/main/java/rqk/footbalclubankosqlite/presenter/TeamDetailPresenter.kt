package rqk.footbalclubankosqlite.presenter

import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rqk.footbalclubankosqlite.api.ApiRespository
import rqk.footbalclubankosqlite.api.TheSportDBApi
import rqk.footbalclubankosqlite.interfac.TeamDetailView
import rqk.footbalclubankosqlite.model.TeamResponse

class TeamDetailPresenter(
    private val view: TeamDetailView,
    private val apiRepository: ApiRespository,
    private val gson: Gson
) {

    fun getTeamDetail(teamId: String) {
        view.showLoading()
        doAsync {
            val data = gson.fromJson(
                apiRepository
                    .doRequest(TheSportDBApi.getTeamDetail(teamId)),
                TeamResponse::class.java
            )

            uiThread {
                view.hideLoading()
                view.showTeamDetail(data.teams)
            }
        }
    }
}