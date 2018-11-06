package rqk.footbalclubankosqlite.view

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import rqk.footbalclubankosqlite.R
import rqk.footbalclubankosqlite.R.color.colorAccent
import rqk.footbalclubankosqlite.R.color.colorPrimaryText
import rqk.footbalclubankosqlite.R.drawable.ic_add_to_favorites
import rqk.footbalclubankosqlite.R.drawable.ic_added_to_favorites
import rqk.footbalclubankosqlite.R.id.add_to_favorite
import rqk.footbalclubankosqlite.R.menu.detail_menu
import rqk.footbalclubankosqlite.api.ApiRespository
import rqk.footbalclubankosqlite.dbhelper.database
import rqk.footbalclubankosqlite.interfac.TeamDetailView
import rqk.footbalclubankosqlite.model.Favorite
import rqk.footbalclubankosqlite.model.Team
import rqk.footbalclubankosqlite.presenter.TeamDetailPresenter
import rqk.footbalclubankosqlite.utils.invisible
import rqk.footbalclubankosqlite.utils.visible

class TeamDetailActivity : AppCompatActivity(), TeamDetailView {

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var teamBadge: ImageView
    private lateinit var teamName: TextView
    private lateinit var teamFormedYear: TextView
    private lateinit var teamStadium: TextView
    private lateinit var teamDescription: TextView

    private lateinit var presenter: TeamDetailPresenter
    private lateinit var teams: Team
    private lateinit var id: String

    private var menuItem: Menu? = null
    private var isFavorite: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        id = intent.getStringExtra("id")
        supportActionBar?.title = getString(R.string.team_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        linearLayout {
            lparams(width = matchParent, height = wrapContent)
            orientation = LinearLayout.VERTICAL
            backgroundColor = Color.WHITE

            swipeRefresh = swipeRefreshLayout {
                setColorSchemeResources(
                    colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
                )

                scrollView {
                    isVerticalScrollBarEnabled = false
                    relativeLayout {
                        lparams(width = matchParent, height = wrapContent)

                        linearLayout {
                            lparams(width = matchParent, height = wrapContent)
                            padding = dip(10)
                            orientation = LinearLayout.VERTICAL
                            gravity = Gravity.CENTER_HORIZONTAL

                            teamBadge = imageView {}.lparams(height = dip(75))

                            teamName = textView {
                                this.gravity = Gravity.CENTER
                                textSize = 20f
                                textColor = ContextCompat.getColor(context, colorAccent)
                            }.lparams {
                                topMargin = dip(5)
                            }

                            teamFormedYear = textView {
                                this.gravity = Gravity.CENTER
                            }

                            teamStadium = textView {
                                this.gravity = Gravity.CENTER
                                textColor = ContextCompat.getColor(context, colorPrimaryText)
                            }

                            teamDescription = textView().lparams {
                                topMargin = dip(20)
                            }
                        }
                        progressBar = progressBar {
                        }.lparams {
                            centerHorizontally()
                        }
                    }
                }
            }
        }

        favoriteState()
        val request = ApiRespository()
        val gson = Gson()
        presenter = TeamDetailPresenter(this, request, gson)
        presenter.getTeamDetail(id)

        swipeRefresh.onRefresh {
            presenter.getTeamDetail(id)
        }
    }

    private fun favoriteState(){
        database.use {
            val result = select(Favorite.TABLE_FAVORITE)
                .whereArgs("(TEAM_ID = {id})",
                    "id" to id)
            val favorite = result.parseList(classParser<Favorite>())
            if (!favorite.isEmpty()) isFavorite = true
        }
    }

    override fun showLoading() {
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.invisible()
    }

    override fun showTeamDetail(data: List<Team>) {
        teams = Team(
            data[0].teamId,
            data[0].teamName,
            data[0].teamBadge
        )
        swipeRefresh.isRefreshing = false
        Glide.with(this).load(data[0].teamBadge).into(teamBadge)
        teamName.text = data[0].teamName
        teamDescription.text = data[0].teamDescription
        teamFormedYear.text = data[0].teamFormedYear
        teamStadium.text = data[0].teamStadium
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(detail_menu, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            add_to_favorite -> {
                if (isFavorite) removeFromFavorite() else addToFavorite()

                isFavorite = !isFavorite
                setFavorite()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addToFavorite() {
        try {
            database.use {
                insert(
                    Favorite.TABLE_FAVORITE,
                    Favorite.TEAM_ID to teams.teamId,
                    Favorite.TEAM_NAME to teams.teamName,
                    Favorite.TEAM_BADGE to teams.teamBadge
                )
            }
            snackbar(swipeRefresh, "Added to favorite").show()
        } catch (e: SQLiteConstraintException) {
            snackbar(swipeRefresh, e.localizedMessage).show()
        }
    }

    private fun removeFromFavorite(){
        try {
            database.use {
                delete(Favorite.TABLE_FAVORITE, "(TEAM_ID = {id})",
                    "id" to id)
            }
            snackbar(swipeRefresh, "Removed to favorite").show()
        } catch (e: SQLiteConstraintException){
            snackbar(swipeRefresh, e.localizedMessage).show()
        }
    }

    private fun setFavorite() {
        if (isFavorite)
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, ic_added_to_favorites)
        else
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, ic_add_to_favorites)
    }
}
