package com.example.musix

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.net.Uri
import android.nfc.Tag
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import java.io.FileNotFoundException
import java.io.IOException
import java.text.BreakIterator
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

class MusicService : Service(),AudioManager.OnAudioFocusChangeListener {

    @RequiresApi(Build.VERSION_CODES.M)
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = MyMediaPlayer.instance
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    @RequiresApi(Build.VERSION_CODES.M)


    inner class MyBinder: Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(context: Context,songsList : ArrayList<AudioModel>, playPauseBtn : Int, index : Int,likeUnlikeBtn : Int){
        mediaSession = MediaSessionCompat(context, "My Music")

        val intent = Intent(this, MusicPlayerFragment::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val likeIntent = Intent(context,NotificationReciever::class.java).setAction(ApplicationClass.LIKE)
        val prevIntent = Intent(context,NotificationReciever::class.java).setAction(ApplicationClass.PREVIOUS)
        val nextIntent = Intent(context,NotificationReciever::class.java).setAction(ApplicationClass.NEXT)
        val playIntent = Intent(context,NotificationReciever::class.java).setAction(ApplicationClass.PLAY)
        val exitIntent = Intent(context,NotificationReciever::class.java).setAction(ApplicationClass.EXIT)
        val likePendingIntent = PendingIntent.getBroadcast(context,0,likeIntent,PendingIntent.FLAG_IMMUTABLE)
        val prevPendingIntent = PendingIntent.getBroadcast(context,0,prevIntent,PendingIntent.FLAG_IMMUTABLE)
        val nextPendingIntent = PendingIntent.getBroadcast(context,0,nextIntent,PendingIntent.FLAG_IMMUTABLE)
        val playPendingIntent = PendingIntent.getBroadcast(context,0,playIntent,PendingIntent.FLAG_IMMUTABLE)
        val exitPendingIntent = PendingIntent.getBroadcast(context,0,exitIntent,PendingIntent.FLAG_IMMUTABLE)
        val currentSong = songsList[index]
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, currentSong.albumId)
        val largeIconBitmap : Bitmap= try{
            MediaStore.Images.Media.getBitmap(context.contentResolver, artUric)
        }catch (e:FileNotFoundException){
            BitmapFactory.decodeResource(resources,R.drawable.resizednew)
        }

        val notification = NotificationCompat.Builder(context,ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(currentSong.title)
            .setContentText(currentSong.artist)
            .setSmallIcon(R.drawable.iconpic)
            .setLargeIcon(largeIconBitmap)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .addAction(likeUnlikeBtn,"like",likePendingIntent)
            .addAction(R.drawable.notify_prev,"previous",prevPendingIntent)
            .addAction(playPauseBtn,"Pause",playPendingIntent)
            .addAction(R.drawable.notify_next,"Next",nextPendingIntent)
            .addAction(R.drawable.exit,"Exit",exitPendingIntent)
            .build()


        val playbackSpeed = if(MusicPlayer().mediaPlayer!!.isPlaying) 1F else 0F
        mediaSession.setMetadata(MediaMetadataCompat.Builder()
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, MusicPlayer().mediaPlayer!!.duration.toLong())
            .build())
        val playBackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, MusicPlayer().mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .build()
        mediaSession.setPlaybackState(playBackState)
        mediaSession.setCallback(object: MediaSessionCompat.Callback() {

            //called when headphones buttons are pressed
            //currently only pause or play music on button click
            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                if (MusicPlayer().mediaPlayer?.isPlaying == true){
                    MusicPlayer().mediaPlayer?.pause()
                    MusicFragment.binding.playPauseBtnNP.setImageResource(R.drawable.play1)
                    MusicPlayer.musicService!!.showNotification(context,NotificationReciever.list,R.drawable.notify_play,MyMediaPlayer.currentIndex,
                    MusicPlayer().likesong(context,NotificationReciever.list,MyMediaPlayer.currentIndex))

                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }


            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                MusicPlayer().mediaPlayer!!.seekTo(pos.toInt())
                val playBackStateNew = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, MusicPlayer().mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
                mediaSession.setPlaybackState(playBackStateNew)
            }
        })

        startForeground(4, notification)
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(this, "My Music")
        return myBinder
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    fun musicResource(context: Context, List : ArrayList<AudioModel>,Index : Int) {
//        val currentsong = List[Index]
//        val uri = Uri.parse("content://media//external//audio/albumart")
//        val artUric = Uri.withAppendedPath(uri, currentsong.albumId)
//
//        MusicPlayer().setResourcesWithMusic(context,List,Index)
//
//        MusicPlayer.musicService!!.showNotification(applicationContext,List,R.drawable.notify_pause,Index)
//
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

             return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //pause music
            MusicPlayerFragment.binding.pause.setImageResource(R.drawable.play1)
            MusicFragment.binding.playPauseBtnNP.setImageResource(R.drawable.play1)
            MusicPlayer().mediaPlayer!!.pause()
            showNotification(
                this,
                NotificationReciever.list,
                R.drawable.notify_play,
                MyMediaPlayer.currentIndex,
                MusicPlayer().likesong(
                    this,
                    NotificationReciever.list, MyMediaPlayer.currentIndex
                )
            )

        }
    }
}