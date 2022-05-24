package ru.netology.nmedia.db

object PostsTable {

    const val NAME = "posts"

    val ALL_COLUMN_NAMES by lazy {
        Column.values().map(Column::columnName).toTypedArray()
    }

    val CREATE_SCRIPT = """
        CREATE TABLE $NAME(
        ${Column.ID.columnName} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${Column.AUTHOR.columnName} TEXT NOT NULL,
        ${Column.CONTENT.columnName} TEXT NOT NULL,
        ${Column.CONTENT_VIDEO.columnName} TEXT,
        ${Column.PUBLISHED.columnName} TEXT NOT NULL,
        ${Column.LIKED_BY_ME.columnName} BOOLEN NOT NULL DEFAULT 0,
        ${Column.LIKES.columnName} INTEGER NOT NULL DEFAULT 0,
        ${Column.SHARES.columnName} INTEGER NOT NULL DEFAULT 0,
        ${Column.VIEWS.columnName} INTEGER NOT NULL DEFAULT 0
        );
    """.trimIndent()

    enum class Column(val columnName: String) {
        ID("id"),
        AUTHOR("author"),
        CONTENT("content"),
        CONTENT_VIDEO("contentVideo"),
        PUBLISHED("published"),
        LIKED_BY_ME("likedByMe"),
        LIKES("likes"),
        SHARES("shares"),
        VIEWS("views")
    }
}