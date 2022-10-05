package com.example.musix

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musix.databinding.ActivityQueueListBinding
import java.io.FileNotFoundException

class QueueListActivity : AppCompatActivity() {
    private var binding : ActivityQueueListBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val currentSongInfo = intent.getSerializableExtra("currentSong") as AudioModel
        val uri = Uri.parse("content://media//external//audio/albumart")
        val artUric = Uri.withAppendedPath(uri, currentSongInfo.albumId)
        val db = QueueListDatabase(applicationContext)
        val queueList = db.getQueueSongsList()
        val largeIconBitmap  = try{
            MediaStore.Images.Media.getBitmap(contentResolver, artUric)

        }catch (e: FileNotFoundException) {
            R.drawable.resizednew
        }
        binding?.queueList?.layoutManager = LinearLayoutManager(this)
        binding?.queueList?.adapter = QueueListAdapter(this, queueList )
        Glide.with(applicationContext).load(largeIconBitmap).into(binding?.queueIconView!!)
        binding?.queuemusicTitleText?.text = currentSongInfo.title
        binding?.clearQueue?.setOnClickListener {
            db.deleteQueueDB()

            QueueListAdapter(this,   QueueListDatabase(this).getQueueSongsList())
                .refreshPlayList()
        }
        binding?.queueDown?.setOnClickListener {
            onBackPressed()
        }

    }

}