package com.example.musix

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.ThreadLocalRandom
import kotlin.system.exitProcess

class NotificationReciever : BroadcastReceiver() {
    companion object{
        var list : ArrayList<AudioModel> = ArrayList()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            ApplicationClass.LIKE -> MusicPlayer().addRemoveLike(context!!, list)
            ApplicationClass.PREVIOUS -> {
                MusicPlayer().prevSong(context!!,list,MyMediaPlayer.currentIndex)
            }
            ApplicationClass.PLAY -> MusicPlayer().pauseMusic(context!!, list)
            ApplicationClass.NEXT -> {
                    MusicPlayer().nextSong(context!!,list
                    ,MyMediaPlayer.currentIndex)

            }
            ApplicationClass.EXIT -> {
                MusicPlayer.musicService?.stopForeground(true)
                MusicPlayer.musicService = null
                exitProcess(1)
            }
        }
    }
}
