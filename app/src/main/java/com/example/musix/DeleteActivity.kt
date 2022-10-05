package com.example.musix

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musix.databinding.ActivityDeleteBinding
import java.util.ArrayList

class DeleteActivity : AppCompatActivity() {
    private lateinit var adapter: DeletePLSAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.DeleteRV.layoutManager = LinearLayoutManager(this)
        adapter = DeletePLSAdapter(this,MyMusicActivity.musicPlayList.ref[PlayListFragment.currentPlayListPos].playList )
        binding.DeleteRV.adapter = adapter

    binding?.backBtn?.setOnClickListener{
        super.onBackPressed()
    }
    }
    companion object{
        lateinit var musicListSearch: ArrayList<AudioModel>
        lateinit var binding: ActivityDeleteBinding
        var search: Boolean = false

    }
}