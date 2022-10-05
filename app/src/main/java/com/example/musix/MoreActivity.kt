package com.example.musix

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musix.databinding.ActivityMoreBinding
import com.example.musix.databinding.AddPlaylistCustomDialogBinding
import com.example.musix.databinding.AddToPlaylistBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*

class MoreActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        audioModel = (intent.getSerializableExtra("AudioModel") as AudioModel?)!!
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, audioModel!!.albumId)

        binding?.moreTitle?.text = audioModel.title
        binding?.moreTitle?.text = audioModel.title
        binding?.moreTitle?.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding?.moreTitle?.marqueeRepeatLimit = -1
        binding?.moreTitle?.isSelected = true
        Glide.with(this).load(artUric).error(R.drawable.resizednew).into(binding?.moreImg!!)
        val db = LikedSongsDatabase(this)
        if (db.getLikedSongsList().contains(audioModel)){
            binding?.LikeText?.text = "Dislike"
            binding?.moreLike?.setImageResource(R.drawable.liked)
        }


            binding?.QueueImg?.setOnClickListener {
            QueueListDatabase(this).addQueueSong(audioModel)
            Snackbar.make(window.decorView.findViewById(R.id.QueueImg),
                "Added to Queue",1000).show()
        }
        binding?.QueueText?.setOnClickListener {
            QueueListDatabase(this).addQueueSong(audioModel)
            Snackbar.make(window.decorView.findViewById(R.id.QueueImg),
                "Added to Queue",1000).show()
        }

        binding?.moreLike?.setOnClickListener {
           onClickLike()
        }
        binding?.LikeText?.setOnClickListener {
            onClickLike()
            }


        binding?.PlayListBtn?.setOnClickListener {
            val editor = getSharedPreferences("PLAYLISTS", MODE_PRIVATE)
        val jsonStringPlayList = editor.getString("MusicPlayList",null)
        if (jsonStringPlayList != null) {
            val dataPlayList: MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)
            val customDialog = Dialog(this)
             dialogBinding = AddToPlaylistBinding.inflate(layoutInflater)
            customDialog.setContentView(dialogBinding.root)
            customDialog.setCanceledOnTouchOutside(true)
            dialogBinding.addPlayList.layoutManager = LinearLayoutManager(this)
            dialogBinding.addPlayList.adapter = addToPlayListDialog(this,dataPlayList.ref)
            customDialog.show()
        }
            else{
            val customDialog = Dialog(this)
             dialogBinding = AddToPlaylistBinding.inflate(layoutInflater)
            customDialog.setContentView(dialogBinding.root)
            customDialog.setCanceledOnTouchOutside(true)
            dialogBinding.addPlayList.visibility = View.INVISIBLE
            dialogBinding.noPlaylist.visibility = View.VISIBLE
            customDialog.show()
            }
        }
        binding?.PLayListText?.setOnClickListener{
            val editor = getSharedPreferences("PLAYLISTS", MODE_PRIVATE)
            val jsonStringPlayList = editor.getString("MusicPlayList",null)
            if (jsonStringPlayList != null) {
                val dataPlayList: MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)
                val customDialog = Dialog(this)
                 dialogBinding = AddToPlaylistBinding.inflate(layoutInflater)
                customDialog.setContentView(dialogBinding.root)
                customDialog.setCanceledOnTouchOutside(true)
                dialogBinding.addPlayList.layoutManager = LinearLayoutManager(this)
                dialogBinding.addPlayList.adapter = addToPlayListDialog(this,dataPlayList.ref)
                customDialog.show()
            }
            else{
                val customDialog = Dialog(this)
                 dialogBinding = AddToPlaylistBinding.inflate(layoutInflater)
                customDialog.setContentView(dialogBinding.root)
                customDialog.setCanceledOnTouchOutside(true)
                dialogBinding.addPlayList.visibility = View.INVISIBLE
                dialogBinding.noPlaylist.visibility = View.VISIBLE
                customDialog.show()
            }
        }



    }

    @RequiresApi(Build.VERSION_CODES.M)
    private  fun onClickLike(){
        MyMusicActivity.binding?.nomusic?.visibility = View.INVISIBLE
        val db = LikedSongsDatabase(this)
        if (db.getLikedSongsList().contains(audioModel)){
            binding?.LikeText?.text = "Like"
            binding?.moreLike?.setImageResource(R.drawable.unliked)
            db.removeLikedSong(audioModel)
            Snackbar.make(window.decorView.findViewById(R.id.moreLike),
                "Removed from Liked Songs",1000).show()
            try {

            if (MusicPlayer().mediaPlayer!!.isPlaying){
                MusicPlayer.musicService?.showNotification(this,NotificationReciever.list,R.drawable.notify_pause,MyMediaPlayer.currentIndex,
                    MusicPlayer().likesong(this,NotificationReciever.list,MyMediaPlayer.currentIndex))
            }else{
                MusicPlayer.musicService?.showNotification(this,NotificationReciever.list,R.drawable.notify_play,MyMediaPlayer.currentIndex,
                    MusicPlayer().likesong(this,NotificationReciever.list,MyMediaPlayer.currentIndex))
            }
            }
            catch (e:Exception){}
        }
        else {
            binding?.LikeText?.text = "Dislike"
            binding?.moreLike?.setImageResource(R.drawable.liked)
            db.addLikedSong(audioModel)
            Snackbar.make(
                window.decorView.findViewById(R.id.moreLike),
                "Added to Liked Songs", 1000
            ).show()
            try {

                if (MusicPlayer().mediaPlayer!!.isPlaying) {
                    MusicPlayer.musicService?.showNotification(
                        this,
                        NotificationReciever.list,
                        R.drawable.notify_pause,
                        MyMediaPlayer.currentIndex,
                        MusicPlayer().likesong(
                            this,
                            NotificationReciever.list,
                            MyMediaPlayer.currentIndex
                        )
                    )
                } else {
                    MusicPlayer.musicService?.showNotification(
                        this,
                        NotificationReciever.list,
                        R.drawable.notify_play,
                        MyMediaPlayer.currentIndex,
                        MusicPlayer().likesong(
                            this,
                            NotificationReciever.list,
                            MyMediaPlayer.currentIndex
                        )
                    )
                }
            }
            catch (e:Exception){}
        }
        MyMusicActivity.likedSongsAdapter.refreshPlayList()
    }

    companion object{
        lateinit var audioModel: AudioModel
        lateinit var binding : ActivityMoreBinding
        lateinit var dialogBinding : AddToPlaylistBinding
    }
}