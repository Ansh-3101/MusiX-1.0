

package com.example.musix

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.RecyclerCardviewBinding
import com.example.musix.databinding.RecycleritemBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.io.FileNotFoundException


class SelectionAdapter (private val context : Context, private var songsList: ArrayList<AudioModel>)
    :RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {


    class ViewHolder (binding :RecyclerCardviewBinding):RecyclerView.ViewHolder(binding.root){
        val titleTextView = binding.musicTitleText
        val iconImg = binding.iconView
        val more = binding.more
        val addBtn = binding.add


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerCardviewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseRequireInsteadOfGet", "ResourceAsColor")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.more.visibility = View.INVISIBLE

        val songData : AudioModel = songsList[position]
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, songData.albumId)


        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.iconImg)
        holder.titleTextView.text = songData.title
        val editor = context.getSharedPreferences("PLAYLISTS", AppCompatActivity.MODE_PRIVATE)
        val jsonStringPlayList = editor.getString("MusicPlayList",null)
        if (jsonStringPlayList != null)  {
            val dataPlayList: MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)
            if (dataPlayList.ref[PlayListFragment.currentPlayListPos].playList.contains(songData)){
                holder.addBtn.visibility = View.VISIBLE
            }
            else{
                holder.addBtn.visibility = View.INVISIBLE
            }
        }

        holder.itemView.setOnClickListener {
            if (addSong(songsList[position])){
                holder.addBtn.visibility = View.VISIBLE
                Snackbar.make(SelectionActivity.binding.SelectionRV,
                    "Added to ${MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].name}",1000).show()
                PlayListFragment.adapter.refreshPlayList()
                MyMusicActivity.playListAdapter.refreshPlayList()
            }
            else {
                holder.addBtn.visibility = View.INVISIBLE
                Snackbar.make(SelectionActivity.binding.SelectionRV,
                    "Removed from ${MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].name}",1000).show()
                PlayListFragment.adapter.refreshPlayList()
                MyMusicActivity.playListAdapter.refreshPlayList()
            }

            val editor = context.getSharedPreferences("PLAYLISTS", AppCompatActivity.MODE_PRIVATE).edit()
            val jsonStringPlayList = GsonBuilder().create().toJson(MyMusicActivity.musicPlayList)
            editor.putString("MusicPlayList",jsonStringPlayList)
            editor.apply()
        }


    }
    override fun getItemCount(): Int {
        return songsList.size
    }

    fun updateMusicList(searchList : ArrayList<AudioModel>){
        songsList = ArrayList()
        songsList.addAll(searchList)

        notifyDataSetChanged()

    }


    private fun addSong(song : AudioModel) : Boolean {
        MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList.forEachIndexed{index,music ->
            if (song.albumId == music.albumId){
                MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList.removeAt(index)
                return false
            }

        }
        MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList.add(song)
        return true
    }

}
