package com.example.musix

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musix.databinding.ActivitySelectionBinding
import java.util.ArrayList


class SelectionActivity : AppCompatActivity() {
//    private var binding:ActivitySelectionBinding? = null
    private lateinit var adapter: SelectionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.SelectionRV?.layoutManager = LinearLayoutManager(this)
        adapter = SelectionAdapter(this,MyMusicActivity.List )
        binding?.SelectionRV?.adapter = adapter


        binding?.backBtn?.setOnClickListener{
            super.onBackPressed()
        }

        binding.searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MyMusicActivity.List)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)


                    search = true
                    adapter.notifyDataSetChanged()

                    adapter.updateMusicList(musicListSearch)
                }
                return true

            }

        })

    }
    companion object{
        lateinit var musicListSearch: ArrayList<AudioModel>
        lateinit var binding: ActivitySelectionBinding
        var search: Boolean = false
    }
}