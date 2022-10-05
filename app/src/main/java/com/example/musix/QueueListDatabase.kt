package com.example.musix

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.Transliterator
import android.preference.TwoStatePreference
import android.util.Log
import kotlin.math.truncate

class QueueListDatabase(context: Context) :SQLiteOpenHelper(context ,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME ="QueueSongsDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_QUEUE_SONGS = "QueueSongsTable"
        private const val QUEUE_PATH  ="path"
        private const val QUEUE_TITLE  ="title"
        private const val QUEUE_DURATION  ="duration"
        private const val QUEUE_ALBUM_ID  ="album_id"
        private const val QUEUE_ALBUM_NAME  ="album_name"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_QUEUE_SONGS_DATABASE =("CREATE TABLE " + QueueListDatabase.TABLE_QUEUE_SONGS +  " ("
                + QUEUE_PATH + " TEXT PRIMARY KEY,"
                + QUEUE_TITLE + " TEXT,"
                + QUEUE_DURATION + " TEXT,"
                + QUEUE_ALBUM_ID + " TEXT,"
                + QUEUE_ALBUM_NAME + " TEXT)")
        db!!.execSQL(CREATE_QUEUE_SONGS_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_QUEUE_SONGS")
        onCreate(db)
    }
    fun addQueueSong(audio :AudioModel) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(QUEUE_PATH,audio.path)
        contentValues.put(QUEUE_TITLE,audio.title)
        contentValues.put(QUEUE_DURATION,audio.duration)
        contentValues.put(QUEUE_ALBUM_ID,audio.albumId)
        contentValues.put(QUEUE_ALBUM_NAME,audio.artist)

        val result =db.insert(TABLE_QUEUE_SONGS,null,contentValues)
        db.close()
        return result
    }
    fun removeQueueSong(audio: AudioModel) {
        val db = this.writableDatabase
        val success = db.delete(TABLE_QUEUE_SONGS, QUEUE_ALBUM_ID + "=" + audio.albumId,null)
        db.close()

    }
    @SuppressLint("Recycle")
    fun deleteQueueDB(){
        val query = "DROP TABLE $TABLE_QUEUE_SONGS"
       val db = this.writableDatabase
       db.rawQuery(query,null)
        db.execSQL(query)
        val CREATE_QUEUE_SONGS_DATABASE =("CREATE TABLE " + QueueListDatabase.TABLE_QUEUE_SONGS +  " ("
                + QUEUE_PATH + " TEXT PRIMARY KEY,"
                + QUEUE_TITLE + " TEXT,"
                + QUEUE_DURATION + " TEXT,"
                + QUEUE_ALBUM_ID + " TEXT,"
                + QUEUE_ALBUM_NAME + " TEXT)")
        db!!.execSQL(CREATE_QUEUE_SONGS_DATABASE)
    }

    @SuppressLint("Range")
    fun getQueueSongsList() : ArrayList<AudioModel> {
        val queueSongList : ArrayList<AudioModel> =ArrayList()

        val selectQuery ="SELECT * FROM $TABLE_QUEUE_SONGS"

        val db =this.readableDatabase
        try {
            val cursor : Cursor = db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst()){
                do {
                    val likedSong = AudioModel(
                        cursor.getString(cursor.getColumnIndex(QUEUE_PATH)),
                        cursor.getString(cursor.getColumnIndex(QUEUE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(QUEUE_DURATION)),
                        cursor.getString(cursor.getColumnIndex(QUEUE_ALBUM_ID)),
                        cursor.getString(cursor.getColumnIndex(QUEUE_ALBUM_NAME))
                    )
                    queueSongList.add(likedSong)
                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return queueSongList
    }


}