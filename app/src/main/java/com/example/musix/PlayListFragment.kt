package com.example.musix

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.musix.databinding.FragmentMusicBinding
import com.example.musix.databinding.FragmentPlayListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.io.FileNotFoundException


class PlayListFragment : Fragment() {

    companion object {
        lateinit var binding:FragmentPlayListBinding
        lateinit var adapter : PlaylistingAdapter
        lateinit var dataPlayList : MusicPlayList
        var currentPlayListPos : Int = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
         val view = inflater.inflate(R.layout.fragment_play_list, container, false)
        binding = FragmentPlayListBinding.bind(view)
//        PlayListActivity.currentPlayListPos = intent.extras?.get("index") as Int
//        adapter = PlaylistingAdapter(requireContext(),MyMusicActivity.musicPlayList.ref[currentPlayListPos].playList)
//        binding?.PLayListsSongs?.layoutManager = LinearLayoutManager(requireContext())
//        binding?.PLayListsSongs?.adapter =adapter
//        binding?.PlayListName?.text = MyMusicActivity.musicPlayList.ref[currentPlayListPos].name
//        binding?.createdOn?.text =  MyMusicActivity.musicPlayList.ref[currentPlayListPos].createdON
//        if (adapter.itemCount > 0){
//            val uri = Uri.parse("content://media//external//audio/albumart")
//            val artUric = Uri.withAppendedPath(uri,MyMusicActivity.musicPlayList.ref[currentPlayListPos].playList[0].albumId)
//
//            Glide.with(this).load(artUric).error(R.drawable.resizednew).into(binding?.PlayListImage!!)
//        }
//
//        binding?.addSongs?.setOnClickListener{
//            val intent = Intent(requireContext(),SelectionActivity::class.java)
//            startActivity(intent)
//        }
        return view
    }

     fun onresume(context : Context,currentPlayListPos : Int) {
        super.onResume()

        val editor = context.getSharedPreferences("PLAYLISTS", AppCompatActivity.MODE_PRIVATE)
        val jsonStringPlayList = editor.getString("MusicPlayList",null)
        if (jsonStringPlayList != null)  {
             dataPlayList = GsonBuilder().create().fromJson(jsonStringPlayList, MusicPlayList::class.java)

        }
            if (dataPlayList.ref.size > 0) {
                binding?.PlayListName?.text = dataPlayList.ref[currentPlayListPos].name
                binding.PlayListName.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.PlayListName.marqueeRepeatLimit = -1
                binding.PlayListName.isSelected = true
                binding?.createdOn?.text = dataPlayList.ref[currentPlayListPos].createdON

        adapter = PlaylistingAdapter(context,dataPlayList.ref[currentPlayListPos].playList)
        binding?.PLayListsSongs?.layoutManager = LinearLayoutManager(context)
        binding?.PLayListsSongs?.adapter =adapter
//        binding?.PlayListName?.text = MyMusicActivity.musicPlayList.ref[currentPlayListPos].name
//        binding?.createdOn?.text =  MyMusicActivity.musicPlayList.ref[currentPlayListPos].createdON
        if(adapter.itemCount > 0 ){
            val uri = Uri.parse("content://media//external//audio/albumart")
            val artUric1 = Uri.withAppendedPath(uri,MyMusicActivity.musicPlayList.ref[currentPlayListPos].playList[0].albumId)
            Glide.with(context).load(artUric1).error(R.drawable.resizednew).into(binding?.PlayListImage!!)
            Glide.with(context).load(artUric1).error(R.drawable.resizednew).override(2,2).into(binding?.playBG!!)
        }
                else{

            Glide.with(context).load(R.drawable.resizednew).into(binding?.PlayListImage!!)
            Glide.with(context).load(R.drawable.resizednew).override(2,2).into(binding?.playBG!!)
        }



        binding?.addSongs?.setOnClickListener{
            val intent = Intent(context,SelectionActivity::class.java)
            context.startActivity(intent)
        }
        binding?.deleteSongs?.setOnClickListener {
            if (MyMusicActivity.musicPlayList.ref[currentPlayListPos].playList.isEmpty()){
                Snackbar.make(binding.scrollView,
                    "${MyMusicActivity.musicPlayList.ref[currentPlayListPos].name} is empty",1000).show()
            }
            else {
                val intent = Intent(context, DeleteActivity::class.java)
                context.startActivity(intent)
            }
        }

    }


    }


}