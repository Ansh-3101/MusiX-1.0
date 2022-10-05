package com.example.musix

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.AddToPlaylistBinding
import com.example.musix.databinding.QueueRecyclerItemBinding
import com.example.musix.databinding.RecyclerCardviewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder

class addToPlayListDialog (val context: Context, var playlist : ArrayList<PlayList>)
    : RecyclerView.Adapter<addToPlayListDialog.ViewHolder>() {
    class ViewHolder(binding: RecyclerCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.musicTitleText
        val image = binding.iconView
        val more = binding.more
        val add = binding.add

    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return addToPlayListDialog.ViewHolder(
                RecyclerCardviewBinding.inflate(LayoutInflater.from(context),parent,false))
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          holder.more.visibility = View.INVISIBLE
            val uri = Uri.parse("content://media//external//audio/albumart")
            val playData = MyMusicActivity.musicPlayList.ref[holder.adapterPosition].playList[0]
            val artUric = Uri.withAppendedPath(uri, playData.albumId)
            Glide.with(context).load(artUric).error(R.drawable.resizednew).into(holder.image)
           holder.title.text = playlist[holder.adapterPosition].name
            if (playlist[holder.adapterPosition].playList.contains(MoreActivity.audioModel)){
                holder.add.visibility = View.VISIBLE
            }


            holder.itemView.setOnClickListener {
                if (MyMusicActivity.musicPlayList.ref[holder.adapterPosition].playList.contains(MoreActivity.audioModel)){
                    MyMusicActivity.musicPlayList.ref[holder.adapterPosition].playList.remove(MoreActivity.audioModel)
                    holder.add.visibility = View.INVISIBLE
                    Snackbar.make(MoreActivity.dialogBinding.addPlayList, "Removed from ${playlist[holder.adapterPosition].name}"
                        , 1000).show()
                }
                else {
                    MyMusicActivity.musicPlayList.ref[holder.adapterPosition].playList.add(
                        MoreActivity.audioModel
                    )
                    Snackbar.make(
                        MoreActivity.dialogBinding.addPlayList,
                        "Added to ${playlist[holder.adapterPosition].name}",
                        1000
                    ).show()
                    holder.add.visibility = View.VISIBLE
                }
                    val editor = context.getSharedPreferences("PLAYLISTS", AppCompatActivity.MODE_PRIVATE).edit()
                    editor.putString("MusicPlayList",
                        GsonBuilder().create().toJson(MyMusicActivity.musicPlayList))
                    editor.apply()

                    PlaylistingAdapter(context,MyMusicActivity.musicPlayList
                        .ref[PlayListFragment.currentPlayListPos].playList).refreshPlayList()

            }
        }


        override fun getItemCount(): Int {
            return playlist.size
        }



    }

