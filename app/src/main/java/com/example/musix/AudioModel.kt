package com.example.musix
 import java.io.Serializable
data class AudioModel(
        var path: String,
        var title: String,
        var duration: String,
        var albumId: String,
        var artist : String)
        : Serializable

    class PlayList{
            lateinit var name : String
            lateinit var playList : ArrayList<AudioModel>
            lateinit var createdON : String
    }

    class MusicPlayList{
            var ref: ArrayList<PlayList> = ArrayList()
    }


data class MyPlayList(    var name : String,
                     var createdON : String ): Serializable


