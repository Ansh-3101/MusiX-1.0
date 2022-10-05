package com.example.musix

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.RecyclerCardviewBinding
import com.example.musix.databinding.RecycleritemBinding

class LikedSongsAdapter(private val context: Context,private var likedSongsList:ArrayList<AudioModel>):
RecyclerView.Adapter<LikedSongsAdapter.ViewHolder>(){
    class ViewHolder(binding: RecyclerCardviewBinding) :RecyclerView.ViewHolder(binding.root){
        val titleTextView = binding.musicTitleText
        val iconImg = binding.iconView
        val  more  = binding.more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerCardviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dbHandler =LikedSongsDatabase(context)
        val getLikedSongsList = dbHandler.getLikedSongsList()
        likedSongsList = getLikedSongsList
        val likedList : AudioModel = likedSongsList[position]
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, likedList.albumId)


        holder.titleTextView.text = likedList.title

        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.iconImg)


        holder.itemView.setOnClickListener {
            MyMediaPlayer.getInstance()!!.reset()
            MyMediaPlayer.currentIndex = position
            NotificationReciever.list = likedSongsList
            MyMusicActivity.tempList = likedSongsList
            MusicFragment().list = likedSongsList
            MusicPlayerFragment.binding.listname.text = "PLAYING FROM LIKED SONGS"
            MusicPlayer().setResourcesWithMusic(context,likedSongsList,MyMediaPlayer.currentIndex)

        }
        if (LikedSongsDatabase(context).getLikedSongsList().isNotEmpty()){
        holder.more.setOnClickListener {
            val audio: AudioModel = likedSongsList[position]
            val intent = Intent(context, MoreActivity::class.java)
            intent.putExtra("AudioModel", audio)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }

        }
    }

    override fun getItemCount(): Int {
        return likedSongsList.size
    }
    fun refreshPlayList(){
        likedSongsList  = ArrayList()
        likedSongsList.addAll(LikedSongsDatabase(context).getLikedSongsList())
        notifyDataSetChanged()
    }


}