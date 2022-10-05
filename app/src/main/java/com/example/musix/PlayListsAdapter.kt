package com.example.musix

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musix.databinding.DeletePlaylistCustomDialogBinding
import com.example.musix.databinding.PlayCardBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.ThreadLocalRandom

class PlayListsAdapter(private  val context: Context,private var playList: ArrayList<PlayList>)
    :RecyclerView.Adapter<PlayListsAdapter.ViewHolder>() {
    class ViewHolder(binding: PlayCardBinding) : RecyclerView.ViewHolder(binding.root) {
         val playListName  = binding.playlistName
        val playListImage = binding.playlistImg
        val imgLayout = binding.RR
        val playListImage1 = binding.playlistImg1
        val playListImage2 = binding.playlistImg2
        val playListImage3 = binding.playlistImg3
        val playListImage4 = binding.playlistImg4
        val playListDate = binding.playlistDate
        val playListSize = binding.playlistSize
        val playListDeleteBtn = binding.deletePlayList
        val playListPlayBtn = binding.PlayPlayList
        val playListShuffleBtn = binding.ShufflePlayList
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(
           PlayCardBinding.inflate(LayoutInflater.from(context),parent,false)
       )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val editor = context.getSharedPreferences("PLAYLISTS", MODE_PRIVATE)
//        val jsonStringPlayList = editor.getString("MusicPlayList",null)
//        if (jsonStringPlayList != null) {
//            val dataPlayList: MusicPlayList =
//                GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)
//            MyMusicActivity.musicPlayList = dataPlayList

            holder.playListName.text = playList[position].name
            holder.playListName.ellipsize = TextUtils.TruncateAt.MARQUEE
            holder.playListName.marqueeRepeatLimit = -1
            holder.playListName.isSelected = true
            holder.playListDate.text = playList[position].createdON
            holder.playListSize.text = "${playList[position].playList.size.toString()} Songs"
        if (playList[holder.adapterPosition].playList.size > 4) {
            holder.playListImage.visibility = View.INVISIBLE
            holder.imgLayout.visibility  = View.VISIBLE
            val uri = Uri.parse("content://media//external//audio/albumart")
            val artUric1 = Uri.withAppendedPath(uri, playList[holder.adapterPosition].playList[0].albumId)
            val artUric2 = Uri.withAppendedPath(uri, playList[holder.adapterPosition].playList[1].albumId)
            val artUric3 = Uri.withAppendedPath(uri, playList[holder.adapterPosition].playList[2].albumId)
            val artUric4 = Uri.withAppendedPath(uri, playList[holder.adapterPosition].playList[3].albumId)
            Glide.with(context).load(artUric1).error(R.drawable.resizednew).into(holder.playListImage1)
            Glide.with(context).load(artUric2).error(R.drawable.resizednew).into(holder.playListImage2)
            Glide.with(context).load(artUric3).error(R.drawable.resizednew).into(holder.playListImage3)
            Glide.with(context).load(artUric4).error(R.drawable.resizednew).into(holder.playListImage4)

        }
        else{
//            val uri = Uri.parse("content://media//external//audio/albumart")
//            val artUric1 = Uri.withAppendedPath(uri, playList[holder.adapterPosition].playList[0].albumId)
            Glide.with(context).load(R.drawable.resizednew).into(holder.playListImage)

        }

        holder.root.setOnClickListener {

            PlayListFragment.currentPlayListPos = holder.adapterPosition
            MyMusicActivity.binding?.PLFRR?.visibility = View.VISIBLE
            MyMusicActivity.binding?.mainLayout?.visibility = View.INVISIBLE
            MyMusicActivity.binding?.nav?.visibility = View.INVISIBLE
            PlayListFragment().onresume(context,holder.adapterPosition)
        }
        holder.playListDeleteBtn.setOnClickListener{
                val customDialog = Dialog(context)
                val dialogBinding = DeletePlaylistCustomDialogBinding.inflate(LayoutInflater.from(context))

                customDialog.setContentView(dialogBinding.root)
                customDialog.setCanceledOnTouchOutside(true)
                dialogBinding.playListName.text  = playList[holder.adapterPosition].name
                dialogBinding.cancelBtn.setOnClickListener { customDialog.dismiss() }
                dialogBinding.deleteBtn.setOnClickListener {
                    MyMusicActivity().deletePlayList(context,holder.adapterPosition)
                    refreshPlayList()
                    customDialog.dismiss() }
                customDialog.show()
            }

        holder.playListPlayBtn.setOnClickListener {
            if (playList[holder.adapterPosition].playList.size > 0) {
                MyMediaPlayer.getInstance()!!.reset()
                NotificationReciever.list = playList[position].playList
                MyMediaPlayer.currentIndex = 0
                MusicPlayerFragment.binding.listname.text = "PLAYING FROM ${playList[position].name}"
                MusicPlayerFragment.binding.listname.ellipsize = TextUtils.TruncateAt.MARQUEE
                MusicPlayerFragment.binding.listname.marqueeRepeatLimit = -1
                MusicPlayerFragment.binding.listname.isSelected = true
                MusicPlayer().setResourcesWithMusic(context, playList[position].playList, MyMediaPlayer.currentIndex)
            }
            else{
                Snackbar.make(MyMusicActivity.binding?.recyclerView!!,"${playList[holder.adapterPosition].name} is empty",1000).show()
            }
        }
        holder.playListShuffleBtn.setOnClickListener {
            if (playList[holder.adapterPosition].playList.size > 0) {
                MyMediaPlayer.getInstance()!!.reset()
                NotificationReciever.list = playList[position].playList
                MyMediaPlayer.currentIndex = ThreadLocalRandom.current().nextInt(playList[position].playList.size)
                MusicPlayerFragment.binding.listname.text = "PLAYING FROM ${playList[position].name}"
                MusicPlayerFragment.binding.listname.ellipsize = TextUtils.TruncateAt.MARQUEE
                MusicPlayerFragment.binding.listname.marqueeRepeatLimit = -1
                MusicPlayerFragment.binding.listname.isSelected = true
                MusicPlayer().setResourcesWithMusic(context, playList[position].playList, MyMediaPlayer.currentIndex)
            }
            else{
                Snackbar.make(MyMusicActivity.binding?.recyclerView!!,"${playList[holder.adapterPosition].name} is empty",1000).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return playList.size
    }
    fun refreshPlayList(){
        playList = ArrayList()
        playList.addAll(MyMusicActivity.musicPlayList.ref)
        notifyDataSetChanged()
    }
}