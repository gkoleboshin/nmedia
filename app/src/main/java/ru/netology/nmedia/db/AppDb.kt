package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.db.dao.PostDao
import ru.netology.nmedia.db.entity.PostEntity

@Database(
    entities = [
        PostEntity::class
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabace(context).also { instance = it }
            }
        }

        private fun buildDatabace(context: Context) = Room.databaseBuilder(
            context, AppDb::class.java, "app.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}