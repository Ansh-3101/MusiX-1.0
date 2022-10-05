package com.example.musix

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.FragmentMusicBinding
import com.example.musix.databinding.RecyclerCardviewBinding
import com.example.musix.databinding.RecycleritemBinding
import com.google.gson.GsonBuilder
import java.io.FileNotFoundException


class PlaylistingAdapter(private val context : Context, private var songsList: ArrayList<AudioModel>)
    :RecyclerView.Adapter<PlaylistingAdapter.ViewHolder>() {


    class ViewHolder (binding : RecyclerCardviewBinding):RecyclerView.ViewHolder(binding.root){
        val titleTextView = binding.musicTitleText
        val iconImg = binding.iconView
        val more = binding.more
        val del_playlist_song = binding.deletePlayListSongs

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerCardviewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseRequireInsteadOfGet", "SetTextI18n")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val songData : AudioModel = songsList[position]
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, songData.albumId)


        holder.titleTextView.text = songData.title
        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.iconImg)




        holder.itemView.setOnClickListener {

            MyMediaPlayer.getInstance()!!.reset()
            MyMediaPlayer.currentIndex = holder.adapterPosition
            MusicPlayer.currentSong = songsList[holder.adapterPosition]
            NotificationReciever.list = songsList
//            MyMusicActivity.tempList = songsList
            MusicPlayerFragment.binding.listname.text = "PLAYING FROM ${PlayListFragment.dataPlayList.ref[PlayListFragment.currentPlayListPos].name}"
            MusicPlayerFragment.binding.listname.ellipsize = TextUtils.TruncateAt.MARQUEE
            MusicPlayerFragment.binding.listname.marqueeRepeatLimit = -1
            MusicPlayerFragment.binding.listname.isSelected = true
            MusicPlayer().setResourcesWithMusic(context,songsList,MyMediaPlayer.currentIndex)

        }
        holder.more.setOnClickListener {
            val audio : AudioModel = songsList[position]
            val intent = Intent(context,MoreActivity::class.java)
            intent.putExtra("AudioModel",audio)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(context,intent,null)
        }

    }
    override fun getItemCount(): Int {
        return songsList.size
    }
    fun refreshPlayList(){
        songsList = ArrayList()
        songsList.addAll(MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList)
        notifyDataSetChanged()
    }

    fun updateMusicList(searchList : ArrayList<AudioModel>){
        songsList = ArrayList()
        songsList.addAll(searchList)
        notifyDataSetChanged()

    }

}
