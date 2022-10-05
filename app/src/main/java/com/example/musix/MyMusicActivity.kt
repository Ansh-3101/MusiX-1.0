package com.example.musix

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musix.databinding.ActivityMyMusicBinding
import com.example.musix.databinding.AddPlaylistCustomDialogBinding
import com.example.musix.databinding.DeletePlaylistCustomDialogBinding
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.List
import kotlin.system.exitProcess

 class MyMusicActivity : AppCompatActivity()
{
    companion object {

        lateinit var musicListSearch: ArrayList<AudioModel>
        var search: Boolean = false
        var musicPlayList: MusicPlayList = MusicPlayList()
        lateinit var List : ArrayList<AudioModel>
        lateinit var tempList : ArrayList<AudioModel>
        lateinit var playListAdapter : PlayListsAdapter
        lateinit var likedSongsAdapter: LikedSongsAdapter
        lateinit var adapter: MusicListAdapter
        lateinit var likeDB : LikedSongsDatabase
        @SuppressLint("StaticFieldLeak")
        var binding: ActivityMyMusicBinding? = null
    }
    var ctext = this
    var songsList = ArrayList<AudioModel>()
    private lateinit var toggle: ActionBarDrawerToggle
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Recycle", "Range", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMusicBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val recyclerView = binding!!.recyclerView
        val nomusic: TextView = findViewById(R.id.nomusic)


//       WindowCompat.setDecorFitsSystemWindows(window,false)
            binding?.nowPlaying!!.visibility = View.INVISIBLE
            musicPlayList = MusicPlayList()
        likedSongsAdapter = LikedSongsAdapter(this, LikedSongsDatabase(this).getLikedSongsList())

        val editor = getSharedPreferences("PLAYLISTS", MODE_PRIVATE)
        val jsonStringPlayList = editor.getString("MusicPlayList",null)
        if (jsonStringPlayList != null)  {
            val dataPlayList: MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)
            musicPlayList = dataPlayList
        }


        binding?.navView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navExit -> {
                    val customDialog = Dialog(this)
                    val dialogBinding = DeletePlaylistCustomDialogBinding.inflate(LayoutInflater.from(this))

                    customDialog.setContentView(dialogBinding.root)
                    customDialog.setCanceledOnTouchOutside(true)
                    dialogBinding.mainText.text = "Are you sure want to Exit Musix "
                    dialogBinding.cancelBtn.setOnClickListener { customDialog.dismiss() }
                    dialogBinding.deleteBtn.text = "EXIT"
                    dialogBinding.deleteBtn.setOnClickListener {
                        MusicPlayer.musicService?.stopForeground(true)
                        MusicPlayer.musicService = null
                        customDialog.dismiss()
                        exitProcess(1)
                    }
                    customDialog.show()
                }
                R.id.equalizer -> {
                    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    if ((intent.resolveActivity(packageManager) != null)) {

                        startActivityForResult(intent,1)
                    }
                    else{
                        Snackbar.make(binding?.root!!,"No Equalizer found",1000).show()
                    }}
                R.id.About -> startActivity(Intent(this, AboutActivity::class.java))
            }
            true
        }
        binding?.menu?.setOnClickListener {
            binding?.drawer?.openDrawer(GravityCompat.START,true)
        }

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null, null
        )
        while (cursor!!.moveToNext()) {
            val songData = AudioModel(
                cursor.getString(1),
                cursor.getString(0),
                cursor.getString(2),
                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString(),
                cursor.getString(4)
            )
            if (File(songData.path).exists()) {
                songsList.add(songData)
            }
            if (songsList.size == 0) {
                nomusic.visibility = View.VISIBLE
            } else {

                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = MusicListAdapter(applicationContext, songsList)
                recyclerView.adapter = adapter
            }
        }
        cursor.close()
        List = songsList
        tempList = songsList
        val dbHandler = LikedSongsDatabase(this@MyMusicActivity)
        val getLikedSongsList = dbHandler.getLikedSongsList()




        binding?.addSongs?.setOnClickListener {

            val customDialog = Dialog(this)
            val dialogBinding = AddPlaylistCustomDialogBinding.inflate(layoutInflater)
            customDialog.setContentView(dialogBinding.root)
            customDialog.setCanceledOnTouchOutside(true)
            dialogBinding.createPlayList.setOnClickListener {
                if (dialogBinding.playListName.text.isNullOrEmpty()) {

                        Snackbar.make(findViewById(R.id.recyclerView),
                            "Name cannot be empty",1000).show()
                } else {
                    val playListName = dialogBinding.playListName.text
                    val calendar = Calendar.getInstance().time
                    val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
                    val createdOn = sdf.format(calendar)
                    addPlaylist(playListName.toString(),createdOn.toString())
                    Snackbar.make(findViewById(R.id.recyclerView),
                        "$playListName : ${sdf.format(calendar)}",1000).show()

                }
                customDialog.dismiss()
            }
            customDialog.show()
        }




        binding!!.Home.setOnClickListener {
//            List = songsList
            binding!!.Home.setImageResource(R.drawable.greenhome)
            binding!!.Like.setImageResource(R.drawable.unliked)
            binding!!.PLayList.setImageResource(R.drawable.playlist)
            binding!!.HomeBarLL.visibility = View.VISIBLE
            binding!!.LikeBarLL.visibility = View.INVISIBLE
            binding!!.PlayListBarLL.visibility = View.INVISIBLE
            binding?.recyclerView2?.visibility = View.INVISIBLE
            binding?.recyclerView?.visibility = View.VISIBLE
//            recyclerView.layoutManager = LinearLayoutManager(this)
//            recyclerView.adapter = MusicListAdapter(this, List)
        }

        binding!!.Like.setOnClickListener {

            binding!!.Home.setImageResource(R.drawable.home1)
            binding!!.Like.setImageResource(R.drawable.greenlike)
            binding!!.PLayList.setImageResource(R.drawable.playlist)
            binding!!.HomeBarLL.visibility = View.INVISIBLE
            binding!!.LikeBarLL.visibility = View.VISIBLE
            binding!!.PlayListBarLL.visibility = View.INVISIBLE

            if (getLikedSongsList.size == 0) {
                binding!!.nomusic.visibility = View.VISIBLE
            } else {
                binding!!.nomusic.visibility = View.INVISIBLE
            }
            binding?.recyclerView2?.visibility = View.VISIBLE
            binding?.recyclerView?.visibility = View.INVISIBLE
            binding?.recyclerView2!!.layoutManager = LinearLayoutManager(this)
            binding?.recyclerView2!!.adapter = likedSongsAdapter
        }

        binding!!.PLayList.setOnClickListener {
            binding!!.Home.setImageResource(R.drawable.home1)
            binding!!.Like.setImageResource(R.drawable.unliked)
            binding!!.PLayList.setImageResource(R.drawable.ic_round_playlist_play_24)
            binding!!.HomeBarLL.visibility = View.INVISIBLE
            binding!!.LikeBarLL.visibility = View.INVISIBLE
            binding!!.PlayListBarLL.visibility = View.VISIBLE
            binding?.recyclerView?.visibility = View.INVISIBLE
            binding?.recyclerView2?.visibility = View.VISIBLE
            binding?.recyclerView2?.layoutManager = GridLayoutManager(this,1)
            playListAdapter = PlayListsAdapter(this, playList = musicPlayList.ref)
            binding?.recyclerView2?.adapter = playListAdapter
        }

        binding!!.likeShuffleBtn.setOnClickListener {
            val likeList = LikedSongsDatabase(this).getLikedSongsList()
           if (likeList.size > 0) {
               MyMediaPlayer.getInstance()!!.reset()
               MyMediaPlayer.currentIndex =
                   ThreadLocalRandom.current().nextInt(likeList.size)
               MusicPlayer().setResourcesWithMusic(
                   this,
                   likeList,
                   MyMediaPlayer.currentIndex
               )
           }else{
               Snackbar.make(binding?.recyclerView!!,"List is empty",1000).show()
           }
        }

        binding!!.likePlayBtn.setOnClickListener {
            val likeList = LikedSongsDatabase(this).getLikedSongsList()
            if (likeList.size > 0) {
                MyMediaPlayer.getInstance()!!.reset()
                MyMediaPlayer.currentIndex = 0
                MusicPlayer().setResourcesWithMusic(
                    this,
                    likeList,
                    MyMediaPlayer.currentIndex
                )
            }
            else{
                Snackbar.make(binding?.recyclerView!!,"List is empty",1000).show()
            }

        }
        val searchView = binding?.searchView

        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    search = true
                    for (song in songsList)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                            adapter.notifyDataSetChanged()

                            adapter.updateMusicList(musicListSearch)
                }
                return true
            }
        })
    }
    fun refreshLikedList(){
        var adapter = LikedSongsAdapter(this, List)
        adapter.notifyDataSetChanged()
    }

    fun deletePlayList(context: Context, position :Int){
        musicPlayList.ref.removeAt(position)
        val editor = context.getSharedPreferences("PLAYLISTS", MODE_PRIVATE).edit()
        editor.putString("MusicPlayList",GsonBuilder().create().toJson(musicPlayList))
        editor.apply()
    }
    private fun addPlaylist(name: String , createdOn:String){
        var playlistsExists = false
        for (i in musicPlayList.ref){
            if (name == i.name){
                playlistsExists = true
                break
            }
        }
        if (playlistsExists){
            Snackbar.make(findViewById(R.id.recyclerView),
                "PlayList already exists",1000).show()
        }
        else{
            val tempPlayList = PlayList()
            tempPlayList.name = name
            tempPlayList.playList = ArrayList()
            tempPlayList.createdON = createdOn
            musicPlayList.ref.add(tempPlayList)
            playListAdapter.refreshPlayList()
            val editor = getSharedPreferences("PLAYLISTS", MODE_PRIVATE).edit()
            editor.putString("MusicPlayList",GsonBuilder().create().toJson(musicPlayList))
            editor.apply()
        }
    }


    override fun onBackPressed() {
        if(binding?.MPFRR?.visibility == View.VISIBLE || binding?.PLFRR?.visibility == View.VISIBLE ){
            binding?.MPFRR?.visibility = View.INVISIBLE
            binding?.nav?.visibility = View.VISIBLE
            binding?.navCL?.visibility = View.VISIBLE
            binding?.PLFRR?.visibility = View.INVISIBLE
            binding?.mainLayout?.visibility = View.VISIBLE
            binding?.barLL?.visibility = View.VISIBLE
        }

        else{
            val customDialog = Dialog(this)
            val dialogBinding = DeletePlaylistCustomDialogBinding.inflate(LayoutInflater.from(this))

            customDialog.setContentView(dialogBinding.root)
            customDialog.setCanceledOnTouchOutside(true)
            dialogBinding.mainText.text = "Are you sure want to Exit Musix "
            dialogBinding.cancelBtn.setOnClickListener { customDialog.dismiss() }
            dialogBinding.deleteBtn.text = "EXIT"
            dialogBinding.deleteBtn.setOnClickListener {
                customDialog.dismiss()
            super.onBackPressed()
            }
            customDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        super.onDestroy()
        MusicPlayer.musicService!!.stopForeground(true)
        MusicPlayer.musicService = null
        exitProcess(1)

    }
}


