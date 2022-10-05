package com.example.musix

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.RecyclerCardviewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder

class DeletePLSAdapter (private val context : Context, private var songsList: ArrayList<AudioModel>)
    : RecyclerView.Adapter<DeletePLSAdapter.ViewHolder>() {


    class ViewHolder (binding : RecyclerCardviewBinding): RecyclerView.ViewHolder(binding.root){
        val titleTextView = binding.musicTitleText
        val iconImg = binding.iconView
        val delete = binding.deletePlayListSongs
        val more = binding.more



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerCardviewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseRequireInsteadOfGet", "ResourceAsColor")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.more.visibility = View.INVISIBLE
        holder.iconImg.visibility = View.VISIBLE
        holder.delete.visibility = View.VISIBLE

        val songData : AudioModel = songsList[position]
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, songData.albumId)


        Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.iconImg)
        holder.titleTextView.text = songData.title


        holder.delete.setOnClickListener {

            val song : AudioModel = MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList[holder.adapterPosition]
            MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList.remove(song)
            Snackbar.make(DeleteActivity.binding.DeleteRV,
                "Deleted From ${MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].name}",1000).show()
            val editor = context.getSharedPreferences("PLAYLISTS", AppCompatActivity.MODE_PRIVATE).edit()
            val jsonStringPlayList = GsonBuilder().create().toJson(MyMusicActivity.musicPlayList)
            editor.putString("MusicPlayList",jsonStringPlayList)
            editor.apply()
            PlayListFragment.adapter.refreshPlayList()
            MyMusicActivity.playListAdapter.refreshPlayList()
            refreshPlayList()
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
    fun refreshPlayList(){
        songsList = ArrayList()
        songsList.addAll(MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList)

        notifyDataSetChanged()
    }




}
