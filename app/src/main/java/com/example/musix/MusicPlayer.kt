package com.example.musix

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.VolumeShaper
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("ResourceAsColor")


class MusicPlayer(){

    var mediaPlayer : MediaPlayer? = MyMediaPlayer.instance
    var timer : CountDownTimer? = null
    var repeat : Boolean = false
    var audio : Boolean = false


    @RequiresApi(Build.VERSION_CODES.O)
    fun setResourcesWithMusic(context: Context, List: ArrayList<AudioModel>, Index: Int) {

        val db = QueueListDatabase(context = context)
        val queueList = db.getQueueSongsList()

        val dbHandler = LikedSongsDatabase(context)
        val getLikedSongsList = dbHandler.getLikedSongsList()
        isPlaying = true
        val currentsong = List[Index]
        currentSong = currentsong

        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, currentsong.albumId)

        if (getLikedSongsList.contains(currentsong)) {
            MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.liked)
        } else {
            MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.munliked)
        }


        MyMusicActivity.binding?.nowPlaying?.visibility = View.VISIBLE

        val largeIconBitmap = try { MediaStore.Images.Media.getBitmap(context.contentResolver, artUric) }
                              catch (e: FileNotFoundException) { R.drawable.resizednew }


        // Setting Images and Background of Music icon(fragment and MusicPlayer)
        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(MusicPlayerFragment.binding.musicIconBig)
        Glide.with(context).load(largeIconBitmap).override(3, 4).into(MusicPlayerFragment.binding.bgImage)
//        Glide.with(context).load(largeIconBitmap).override(2, 3).into(MusicPlayerFragment.binding.bgImage)
        Glide.with(context).load(largeIconBitmap).override(2,1).into(MusicFragment.binding.fragmentBgImg)
        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(MusicFragment.binding.songImgNP)



        // Setting Name and Marquee of Music icon(fragment and MusicPlayer)
        MusicFragment.binding.songNameNP.text = currentsong.title
        MusicFragment.binding.playPauseBtnNP.setImageResource(R.drawable.pause1)
        MusicFragment.binding.songNameNP.ellipsize = TextUtils.TruncateAt.MARQUEE
        MusicFragment.binding.songNameNP.marqueeRepeatLimit = -1
        MusicFragment.binding.songNameNP.isSelected = true

        MusicPlayerFragment.binding.songTitle.text = currentsong.title
        MusicPlayerFragment.binding.songTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        MusicPlayerFragment.binding.songTitle.marqueeRepeatLimit = -1
        MusicPlayerFragment.binding.songTitle.isSelected = true
        MusicPlayerFragment.binding.albumnName.text = currentsong.artist

        MusicFragment.binding.songProgress.max = currentSong.duration.toInt()


//        Like and Shuffle Image Selection

        if (!repeat) {
            MusicPlayerFragment.binding.repeat.setImageResource(R.drawable.repeat1)
        } else {
            MusicPlayerFragment.binding.shuffle.setImageResource(R.drawable.green_repeat)
        }

        if (!shuffleMode) {
            MusicPlayerFragment.binding.shuffle.setImageResource(R.drawable.mshuffle)
        } else {
            MusicPlayerFragment.binding.shuffle.setImageResource(R.drawable.greenshuffle)
        }


        mediaPlayer?.setOnCompletionListener { nextSong(context, List, Index) }


        val config  = VolumeShaper.Configuration.Builder()
            .setDuration(2000)
            .setCurve(floatArrayOf(0f,1f), floatArrayOf(0f,1f))
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
            .build()



        MusicPlayerFragment.binding.audio.setOnClickListener {
            if (audio){
                audio = false
                MusicPlayerFragment.binding.audio.setImageResource(R.drawable.audio)
                mediaPlayer?.setVolume(1f,1f)
                mediaPlayer?.createVolumeShaper(config)!!.apply(VolumeShaper.Operation.PLAY)
            }
            else {
                audio = true
                MusicPlayerFragment.binding.audio.setImageResource(R.drawable.vol_off)
                mediaPlayer?.setVolume(0f,0f)
            }
    }


        MusicPlayerFragment.binding.repeat.setOnClickListener {
            if (!repeat){
              MusicPlayerFragment.binding.repeat.setImageResource(R.drawable.green_repeat)
                repeat = true
                Snackbar.make(
                    MusicPlayerFragment.binding.root,
                    "Repeat is On", 1000
                ).show()

            }
            else{
                MusicPlayerFragment.binding.repeat.setImageResource(R.drawable.repeat1)
                repeat = false
                Snackbar.make(
                    MusicPlayerFragment.binding.rrLayout,
                    "Repeat is Off", 1000
                ).show()


            }
        }

        MusicPlayerFragment.binding.pause.setOnClickListener {
            MusicPlayer().pauseMusic(
                context,
                List
            )
        }

        MusicPlayerFragment.binding.shuffle.setOnClickListener {
            if (!shuffleMode) {
                MusicPlayerFragment.binding.shuffle.setImageResource(R.drawable.greenshuffle)
                shuffleMode = true
                Snackbar.make(
                    MusicPlayerFragment.binding.rrLayout,
                    "Shuffle is On", 1000
                ).show()
            }
            else {
                MusicPlayerFragment.binding.shuffle.setImageResource(R.drawable.mshuffle)
                Snackbar.make(
                    MusicPlayerFragment.binding.rrLayout,
                    "Shuffle is Off", 1000
                ).show()
                shuffleMode = false

            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            MusicPlayerFragment.binding.likeBtn.setOnClickListener {
                MyMusicActivity.binding?.nomusic?.visibility = View.INVISIBLE
                MyMusicActivity.likeDB = LikedSongsDatabase(context)
                if (MyMusicActivity.likeDB.getLikedSongsList().contains(currentSong)) {
                    MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.munliked)
                    Snackbar.make(
                        MusicPlayerFragment.binding?.rrLayout!!,
                        "Removed from Liked Songs", 1000
                    ).show()
                    MyMusicActivity.likeDB.removeLikedSong(currentSong)
                    if (MusicPlayer().mediaPlayer!!.isPlaying) {
                        musicService?.showNotification(
                            context,
                            List,
                            R.drawable.notify_pause,
                           Index,
                            MusicPlayer().likesong(
                                context,
                                List,
                               Index
                            )
                        )
                    } else {
                        musicService?.showNotification(
                            context,
                                List,
                            R.drawable.notify_play,
                           Index,
                            MusicPlayer().likesong(
                                context,
                                List,
                                Index
                            )
                        )
                    }

                } else {
                    MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.liked)
                    Snackbar.make(
                       MusicPlayerFragment.binding.rrLayout,
                        "Added to Liked Songs", 1000
                    ).show()
                    MyMusicActivity.likeDB.addLikedSong(currentSong)
                    if (MusicPlayer().mediaPlayer!!.isPlaying) {
                        musicService?.showNotification(
                           context,
                            List,
                            R.drawable.notify_pause,
                            Index,
                            MusicPlayer().likesong(
                                context,
                                List,
                               Index
                            )
                        )
                    } else {
                        musicService?.showNotification(
                            context,
                            List,
                            R.drawable.notify_play,
                            Index,
                            MusicPlayer().likesong(
                                context,
                                List,
                                Index
                            )
                        )
                    }

                }
                MyMusicActivity.likedSongsAdapter.refreshPlayList()
            }
        }

        MusicPlayerFragment.binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (MusicPlayer().mediaPlayer != null && b) {
                    MusicPlayer().mediaPlayer!!.seekTo(i)
                    musicService?.showNotification(context!!,NotificationReciever.list,if (MusicPlayer().mediaPlayer!!.isPlaying) R.drawable.notify_pause
                    else R.drawable.notify_play,Index,MusicPlayer().likesong(context!!,List,Index))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        MusicPlayerFragment.binding.previous.setOnClickListener {
            MusicPlayer().prevSong(
                context,
                List,
               Index
            )
        }

        MusicPlayerFragment.binding.next.setOnClickListener {
            MusicPlayer().nextSong(
                context,
                List,
                Index
            )
        }

        MusicFragment.binding.playPauseBtnNP.setOnClickListener {
            MusicPlayer().pauseMusic(
                context,
                List
            )
        }

        MusicFragment.binding.prevBtnNP.setOnClickListener {
            MusicPlayer().prevSong(
                context,
                List,
                Index
            )
        }

        MusicFragment.binding.nextBtnNP.setOnClickListener {
            MusicPlayer().nextSong(
                context,
                List,
                Index
            )
        }


    CoroutineScope(Dispatchers.Main).launch {
        val rotate = RotateAnimation(
            0F, 5400F, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 180000
        rotate.repeatCount = Animation.INFINITE



        MusicPlayerFragment.binding.musicIconBig.startAnimation(rotate)
        MusicPlayerFragment.binding.totalTime.text = convertToMMSS(currentsong.duration)

    }
        playmusic(context, List, Index)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun nextSong(context: Context, songsList: ArrayList<AudioModel>, Index: Int) {

        val db = QueueListDatabase(context)
        val queueList = db.getQueueSongsList()
        if (MyMusicActivity.search){
            MyMusicActivity.search = false
            MyMediaPlayer.currentIndex = ThreadLocalRandom.current().nextInt(MyMusicActivity.List.size)
            setResourcesWithMusic(context,MyMusicActivity.List,MyMediaPlayer.currentIndex)
        } else {
        if (repeat){
            repeat = false
            setResourcesWithMusic(context,songsList,Index)
        }
        else{
            if (queueList.isNotEmpty() && queueList.size>0) queue(context)
            else {
                 if (shuffleMode) {
                      MyMediaPlayer.currentIndex = ThreadLocalRandom.current().nextInt(songsList.size)
                     setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                  }
                 else {
                        if (Index == songsList.size - 1) {
                            MyMediaPlayer.currentIndex = 0
                            mediaPlayer!!.reset()
                            setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                              }
                        else {
                            MyMediaPlayer.currentIndex++
                            mediaPlayer!!.reset()
                            setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                          }
                   }
             }
        }
        }
    }


    fun addRemoveLike(context: Context, list : ArrayList<AudioModel>){
        MyMusicActivity.likeDB = LikedSongsDatabase(context)
        MyMusicActivity.binding?.nomusic?.visibility = View.INVISIBLE
        if (MyMusicActivity.likeDB.getLikedSongsList().contains(currentSong)){
            MyMusicActivity.likeDB.removeLikedSong(currentSong)
            MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.munliked)
            if (MusicPlayer().mediaPlayer!!.isPlaying){
            musicService?.showNotification(context,list,R.drawable.notify_pause,MyMediaPlayer.currentIndex,
            likesong(context,list,MyMediaPlayer.currentIndex))
            }else{
                musicService?.showNotification(context,list,R.drawable.notify_play,MyMediaPlayer.currentIndex,
                likesong(context,list,MyMediaPlayer.currentIndex))
            }
        }
        else {
            MyMusicActivity.likeDB.addLikedSong(currentSong)
            MusicPlayerFragment.binding.likeBtn.setImageResource(R.drawable.liked)
//            MyMusicActivity.binding?.recyclerView?.adapter = LikedSongsAdapter(context,db.getLikedSongsList())
            if (MusicPlayer().mediaPlayer!!.isPlaying) {
                musicService?.showNotification(
                    context,
                    list,
                    R.drawable.notify_pause,
                    MyMediaPlayer.currentIndex,
                    likesong(context, list, MyMediaPlayer.currentIndex)
                )
            } else {
                musicService?.showNotification(
                    context,
                    list,
                    R.drawable.notify_play,
                    MyMediaPlayer.currentIndex,
                    likesong(context, list, MyMediaPlayer.currentIndex)
                )
            }
        }
        MyMusicActivity.likedSongsAdapter.refreshPlayList()



    }

    fun pauseMusic(context: Context,list: ArrayList<AudioModel>) {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            musicService?.showNotification(
                context,
               list,
                R.drawable.notify_play,
                MyMediaPlayer.currentIndex,
            likesong(context,list,MyMediaPlayer.currentIndex))
            MusicFragment.binding.playPauseBtnNP.setImageResource(R.drawable.play1)
            MusicPlayerFragment.binding.musicIconBig.clearAnimation()


        } else {
            mediaPlayer!!.start()
            val rotate1 = RotateAnimation(
                0F, 5400F, Animation.RESTART, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotate1.duration = 180000
            rotate1.repeatCount = Animation.INFINITE

            musicService?.showNotification(
                context,
                list,
                R.drawable.notify_pause,
                MyMediaPlayer.currentIndex,
                likesong(context,list,MyMediaPlayer.currentIndex))
            MusicPlayerFragment.binding.musicIconBig.startAnimation(rotate1)
            MusicFragment.binding.playPauseBtnNP.setImageResource(R.drawable.pause1)
        }
    }

    fun likesong(context: Context,songsList: ArrayList<AudioModel>, Index: Int) : Int{
        val currentsong = songsList[Index]
        val db = LikedSongsDatabase(context)
        if (db.getLikedSongsList().contains(currentsong)) {return R.drawable.liked }
        else {return  R.drawable.unliked }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prevSong(context: Context, songsList: ArrayList<AudioModel>,  Index: Int) {
       if (MyMusicActivity.search){
           MyMusicActivity.search = false
           MyMediaPlayer.currentIndex = ThreadLocalRandom.current().nextInt(MyMusicActivity.List.size)
           setResourcesWithMusic(context,MyMusicActivity.List,MyMediaPlayer.currentIndex)
       } else {
            if (repeat) {
                repeat = false
                setResourcesWithMusic(context, songsList, Index)
            } else {
                if (shuffleMode) {
                    MyMediaPlayer.currentIndex = ThreadLocalRandom.current().nextInt(songsList.size)
                    setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                } else {
                    if (Index == 0) {
                        MyMediaPlayer.currentIndex = songsList.size - 1
                        mediaPlayer!!.reset()
                        setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                    } else {
                        MyMediaPlayer.currentIndex--
                        mediaPlayer!!.reset()
                        setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun playmusic(context: Context ,list: ArrayList<AudioModel>, playIndex: Int) {

        val currentsong = list[playIndex]

        mediaPlayer!!.reset()
        try {
            mediaPlayer!!.setDataSource(currentsong.path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            MusicPlayerFragment.binding.visualizer.setColor(ContextCompat.getColor(context, R.color.white))
            MusicPlayerFragment.binding.visualizer.setPlayer(mediaPlayer!!.audioSessionId)
            MusicPlayerFragment.binding.seekBar.progress = 0
            MusicPlayerFragment.binding.seekBar.max = mediaPlayer!!.duration
        }
        catch (e: IOException) { e.printStackTrace() }

        musicService?.showNotification(context, list, R.drawable.notify_pause,playIndex,likesong(context,list,playIndex))

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun queue(context: Context) {
        mediaPlayer!!.reset()
        MyMediaPlayer.currentIndex = 0
        MusicPlayerFragment.binding.listname.text = "PLAYING FROM QUEUE"
        val db = QueueListDatabase(context)
        val queueList = db.getQueueSongsList()
        MyMusicActivity.List = queueList
        NotificationReciever.list = queueList
        setResourcesWithMusic( context, queueList, MyMediaPlayer.currentIndex)
        val currentSong = queueList[0]
        db.removeQueueSong(currentSong)

    }

    fun convertToMMSS(duration: String): String {
        val millis = duration.toLong()
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    companion object {
        var musicService :MusicService? = null
        lateinit var currentSong:AudioModel
        var shuffleMode : Boolean = false
        var isPlaying : Boolean = false
    }
}
