package rqk.footbalclubankosqlite.interfac

import rqk.footbalclubankosqlite.model.Team

interface TeamsView {
    fun showLoading()
    fun hideLoading()
    fun showTeamList(data: List<Team>)
}