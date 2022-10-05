package com.example.musix

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

open class PlayListDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME ="PLayLists"
        private const val DATABASE_VERSION = 1
        private const val PLAYLIST_SONGS = "PlayListTable"
        private const val PL_PATH  ="path"
        private const val PL_NAME  ="name"
        private const val PL_TIME  ="time"
        private const val PL_SONGS  ="list"
        private const val PL_TITLE  ="title"
        private const val PL_DURATION  ="duration"
        private const val PL_ALBUM_ID  ="album_id"
        private const val PL_ALBUM_NAME  ="album_name"
    }
//    override fun onCreate(db: SQLiteDatabase?) {
//        val CREATE_PLAYLISTS_DATABASE =("CREATE TABLE " + PLAYLIST_SONGS +  " ("
//                + PL_PATH + " TEXT PRIMARY KEY,"
//                + PL_TITLE + " TEXT,"
//                + PL_DURATION + " TEXT,"
//                + PL_ALBUM_ID + " TEXT,"
//                + PL_ALBUM_NAME + " TEXT)")
//        db!!.execSQL(CREATE_PLAYLISTS_DATABASE)
//    }
//
override fun onCreate(db: SQLiteDatabase?) {
    val CREATE_PLAYLISTS_DATABASE =("CREATE TABLE " + PLAYLIST_SONGS +  " ( "
            + PL_NAME + " TEXT PRIMARY KEY ," +
            PL_TIME + " TEXT ," +
           PL_SONGS +  " TEXT )")
    db!!.execSQL(CREATE_PLAYLISTS_DATABASE)
}

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $PLAYLIST_SONGS")
        onCreate(db)
    }

    fun addPLayLists(name : String , createdON : String )  {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PL_NAME,name)
        contentValues.put(PL_TIME,createdON)


        db.insert(PLAYLIST_SONGS,null,contentValues)
        db.close()

    }
    fun removeLikedSong(audio: AudioModel) {
        val db = this.writableDatabase
        db.delete(PLAYLIST_SONGS, PL_ALBUM_ID + "=" + audio.albumId,null)
        db.close()

    }

    @SuppressLint("Range")
    fun getPlayListSongsList() : ArrayList<MyPlayList> {
        val playListSongsList : ArrayList<MyPlayList> = ArrayList()

        val selectQuery ="SELECT * FROM $PLAYLIST_SONGS"

        val db = this.readableDatabase
        try {
            val cursor : Cursor = db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst()){
                do {
                    val playLists = MyPlayList(
                        cursor.getString(cursor.getColumnIndex(PL_NAME)),
                        cursor.getString(cursor.getColumnIndex(PL_TIME)),


                    )
//                        AudioModel(
//                        cursor.getString(cursor.getColumnIndex(PL_PATH)),
//                        cursor.getString(cursor.getColumnIndex(PL_TITLE)),
//                        cursor.getString(cursor.getColumnIndex(PL_DURATION)),
//                        cursor.getString(cursor.getColumnIndex(PL_ALBUM_ID)),
//                        cursor.getString(cursor.getColumnIndex(PL_ALBUM_NAME))
//                    )
                   playListSongsList.add(playLists)
                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return playListSongsList
    }
}

