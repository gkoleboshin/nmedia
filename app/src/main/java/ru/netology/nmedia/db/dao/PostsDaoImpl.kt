package ru.netology.nmedia.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.db.PostsTable
import ru.netology.nmedia.dto.Post

class PostsDaoImpl(
    private val db: SQLiteDatabase
) : PostsDao {

    override fun getAll() = db.query(
        PostsTable.NAME,
        PostsTable.ALL_COLUMN_NAMES,
        null, null, null, null,
        "${PostsTable.Column.ID.columnName} DESC "
    ).use { cursor ->
        List(cursor.count) {
            cursor.moveToNext()
            cursor.toPost()
        }
    }



    override fun insert(post: Post): Post {
        val values = ContentValues().apply {
            put(PostsTable.Column.AUTHOR.columnName, "Me")
            put(PostsTable.Column.CONTENT.columnName, post.content)
            put(PostsTable.Column.CONTENT_VIDEO.columnName, post.contentVideo)
            put(PostsTable.Column.PUBLISHED.columnName, "now")
        }
        val id = db.insert(
            PostsTable.NAME,
            null,
            values
        )
        return db.query(
            PostsTable.NAME,
            PostsTable.ALL_COLUMN_NAMES,
            "${PostsTable.Column.ID.columnName} = ?",
            arrayOf(id.toString()),
            null, null, null
        ).use {
            it.moveToNext()
            it.toPost()
        }
    }

    override fun update(post: Post): Post {
        val values = ContentValues().apply {
            put(PostsTable.Column.AUTHOR.columnName, "Me")
            put(PostsTable.Column.CONTENT.columnName, post.content)
            put(PostsTable.Column.CONTENT_VIDEO.columnName, post.contentVideo)
            put(PostsTable.Column.PUBLISHED.columnName, "now")
        }

        db.update(
            PostsTable.NAME,
            values,
            "${PostsTable.Column.ID.columnName} = ?",
            arrayOf(post.id.toString())
        )
        val id = post.id
        return db.query(
            PostsTable.NAME,
            PostsTable.ALL_COLUMN_NAMES,
            "${PostsTable.Column.ID.columnName} = ?",
            arrayOf(id.toString()),
            null, null, null
        ).use {
            it.moveToNext()
            it.toPost()
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
                UPDATE ${PostsTable.NAME} SET
                    likes = likes + CASE WHEN likedByMe THEN -1 else 1 END,
                    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id=?;
            """.trimIndent(),
            arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostsTable.NAME,
            "${PostsTable.Column.ID.columnName} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
                UPDATE ${PostsTable.NAME} SET
                    shares = shares + 1  
                WHERE id=?;
            """.trimIndent(),
            arrayOf(id)
        )
    }




    override fun viewById(id: Long) {
        db.execSQL(
            """
                UPDATE ${PostsTable.NAME} SET
                    views = views + 1  
                WHERE id=?;
            """.trimIndent(),
            arrayOf(id)
        )
    }

    private fun Cursor.toPost() = Post(
        id = getLong(getColumnIndexOrThrow(PostsTable.Column.ID.columnName)),
        author = getString(getColumnIndexOrThrow(PostsTable.Column.AUTHOR.columnName)),
        content = getString(getColumnIndexOrThrow(PostsTable.Column.CONTENT.columnName)),
        contentVideo = getString(getColumnIndexOrThrow(PostsTable.Column.CONTENT_VIDEO.columnName)),
        published = getString(getColumnIndexOrThrow(PostsTable.Column.PUBLISHED.columnName)),
        likedByMe = getInt(getColumnIndexOrThrow(PostsTable.Column.LIKED_BY_ME.columnName)) != 0,
        likes = getInt((getColumnIndexOrThrow(PostsTable.Column.LIKES.columnName))),
        shares = getInt((getColumnIndexOrThrow(PostsTable.Column.SHARES.columnName))),
        views = getInt((getColumnIndexOrThrow(PostsTable.Column.VIEWS.columnName))),
    )
}

