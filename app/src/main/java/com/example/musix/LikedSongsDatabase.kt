package com.example.musix

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class LikedSongsDatabase(context: Context) :SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME ="LikedSongsDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_LIKED_SONGS = "LikedSongsTable"
        private const val LIKE_PATH  ="path"
        private const val LIKE_TITLE  ="title"
        private const val LIKE_DURATION  ="duration"
        private const val LIKE_ALBUM_ID  ="album_id"
        private const val LIKE_ALBUM_NAME  ="album_name"


    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIKED_SONGS_DATABASE =("CREATE TABLE " + TABLE_LIKED_SONGS +  " ("
                + LIKE_PATH + " TEXT PRIMARY KEY,"
                + LIKE_TITLE + " TEXT,"
                + LIKE_DURATION + " TEXT,"
                + LIKE_ALBUM_ID + " TEXT,"
                + LIKE_ALBUM_NAME + " TEXT)")
        db!!.execSQL(CREATE_LIKED_SONGS_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    db!!.execSQL("DROP TABLE IF EXISTS $TABLE_LIKED_SONGS")
        onCreate(db)
    }

    fun addLikedSong(audio :AudioModel)  {
    val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(LIKE_PATH,audio.path)
        contentValues.put(LIKE_TITLE,audio.title)
        contentValues.put(LIKE_DURATION,audio.duration)
        contentValues.put(LIKE_ALBUM_ID,audio.albumId)
        contentValues.put(LIKE_ALBUM_NAME,audio.artist)
        db.insert(TABLE_LIKED_SONGS,null,contentValues)
        db.close()

    }
    fun removeLikedSong(audio: AudioModel) {
        val db = this.writableDatabase
        db.delete(TABLE_LIKED_SONGS, LIKE_ALBUM_ID + "=" + audio.albumId,null)
        db.close()
        getLikedSongsList()

    }

    @SuppressLint("Range")
    fun getLikedSongsList() : ArrayList<AudioModel> {
        val likedSongList : ArrayList<AudioModel> =ArrayList()

        val selectQuery ="SELECT * FROM $TABLE_LIKED_SONGS"

        val db = this.readableDatabase
        try {
            val cursor : Cursor = db.rawQuery(selectQuery,null)
                if (cursor.moveToFirst()){
                    do {
                        val likedSong = AudioModel(
                            cursor.getString(cursor.getColumnIndex(LIKE_PATH)),
                            cursor.getString(cursor.getColumnIndex(LIKE_TITLE)),
                            cursor.getString(cursor.getColumnIndex(LIKE_DURATION)),
                            cursor.getString(cursor.getColumnIndex(LIKE_ALBUM_ID)),
                            cursor.getString(cursor.getColumnIndex(LIKE_ALBUM_NAME))
                        )
                    likedSongList.add(likedSong)
                    } while (cursor.moveToNext())
                }
            cursor.close()

        }
        catch (e:SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return likedSongList
    }


}