package com.example.musix

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.musix.databinding.FragmentMusicBinding
import com.example.musix.databinding.RecyclerCardviewBinding
import com.example.musix.databinding.RecycleritemBinding
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.io.FileNotFoundException


class MusicListAdapter(private val context : Context, private var songsList: ArrayList<AudioModel>)
    :RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {


    class ViewHolder (binding : RecyclerCardviewBinding):RecyclerView.ViewHolder(binding.root){
        val titleTextView = binding.musicTitleText
        val iconImg = binding.iconView
        val more = binding.more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(RecyclerCardviewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        val songData : AudioModel = songsList[holder.adapterPosition]
        holder.titleTextView.text = songData.title
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, songData.albumId)


              Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.iconImg)

//               val largeIconBitmap = try {
//                   MediaStore.Images.Media.getBitmap(context.contentResolver, artUric)
//
//               } catch (e: FileNotFoundException) {
//                   R.drawable.resizednew
//               }
//               Glide.with(context).load(largeIconBitmap).dontAnimate().diskCacheStrategy(
//                   DiskCacheStrategy.ALL).thumbnail(0.5F).centerCrop().into(holder.iconImg)

//        holder.titleTextView.text = songData.title

//        if (MyMediaPlayer.currentIndex == position){
//                holder.titleTextView.setTextColor((Color.parseColor("#FF03DAC5")))
//        } else {
//            holder.titleTextView.setTextColor(Color.parseColor("#FFFFFF"))
//       }

        holder.itemView.setOnClickListener {
                MyMediaPlayer.getInstance()!!.reset()
                MyMediaPlayer.currentIndex = holder.adapterPosition
                MusicPlayer.currentSong = songsList[holder.adapterPosition]
                NotificationReciever.list = songsList
                MusicPlayerFragment.binding.listname.text = "PLAYING SONGS"
                MusicPlayer().setResourcesWithMusic(context, songsList, MyMediaPlayer.currentIndex)

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

        notifyDataSetChanged()
    }
    fun updateMusicList(searchList : ArrayList<AudioModel>){
        songsList = ArrayList()
        songsList.addAll(searchList)
        notifyDataSetChanged()

    }

}
