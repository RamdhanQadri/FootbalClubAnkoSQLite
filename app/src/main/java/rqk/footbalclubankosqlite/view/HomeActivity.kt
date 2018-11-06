package rqk.footbalclubankosqlite.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*
import rqk.footbalclubankosqlite.R
import rqk.footbalclubankosqlite.R.id.favorites
import rqk.footbalclubankosqlite.R.id.teams
import rqk.footbalclubankosqlite.view.fregment.FavoriteTeamsFragment
import rqk.footbalclubankosqlite.view.fregment.TeamsFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                teams -> {
                    loadTeamsFragment(savedInstanceState)

                }
                favorites -> {
                    loadFavoritesFragment(savedInstanceState)
                }
            }
            true
        }
        bottom_navigation.selectedItemId = teams
    }

    private fun loadTeamsFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, TeamsFragment(), TeamsFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadFavoritesFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, FavoriteTeamsFragment(), FavoriteTeamsFragment::class.java.simpleName)
                .commit()
        }
    }
}
