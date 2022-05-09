package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.db.dao.PostsDao
import ru.netology.nmedia.db.dao.PostsDaoImpl

class AppDb private constructor(db: SQLiteDatabase) {
    val postDao: PostsDao = PostsDaoImpl(db)

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: AppDb(
                    buildDatabace(context, arrayOf(PostsTable.CREATE_SCRIPT))
                ).also { instance = it }
            }
        }

        private fun buildDatabace(context: Context, createTablesScripts: Array<String>) = DbHelper(
            context, 1, "app.db", createTablesScripts,
        ).writableDatabase
    }
}