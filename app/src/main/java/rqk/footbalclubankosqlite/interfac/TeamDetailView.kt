package rqk.footbalclubankosqlite.interfac

import rqk.footbalclubankosqlite.model.Team

interface TeamDetailView {
    fun showLoading()
    fun hideLoading()
    fun showTeamDetail(data: List<Team>)
}