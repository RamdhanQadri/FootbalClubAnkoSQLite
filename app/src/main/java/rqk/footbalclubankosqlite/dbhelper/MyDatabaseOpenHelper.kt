package rqk.footbalclubankosqlite.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import rqk.footbalclubankosqlite.model.Favorite

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "FavoriteTeam.db", null, 1) {
    companion object {
        private var intance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getIntance(ctx: Context): MyDatabaseOpenHelper {
            if (intance == null) {
                intance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return intance as MyDatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(
            Favorite.TABLE_FAVORITE, true,
            Favorite.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Favorite.TEAM_ID to TEXT + UNIQUE,
            Favorite.TEAM_NAME to TEXT,
            Favorite.TEAM_BADGE to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(Favorite.TABLE_FAVORITE, true)
    }
}

// Access property for Context
val Context.database: MyDatabaseOpenHelper get() = MyDatabaseOpenHelper.getIntance(applicationContext)