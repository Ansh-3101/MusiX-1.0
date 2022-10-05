package com.example.musix

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.musix.databinding.FragmentMusicBinding
import com.example.musix.databinding.FragmentMusicPlayerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MusicPlayerFragment : Fragment() {



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_music_player,container,false)
        binding = FragmentMusicPlayerBinding.bind(view)

        MyMusicActivity().runOnUiThread(object : Runnable {
            override fun run() {
                if (MusicPlayer().mediaPlayer != null) {

                    binding.seekBar.progress = (MusicPlayer().mediaPlayer!!.currentPosition)
                    MusicFragment.binding.songProgress.progress = (MusicPlayer().mediaPlayer!!.currentPosition)

                    binding.currentTime.text = convertToMMSS(MusicPlayer().mediaPlayer!!.currentPosition.toString()+"")

                    if (MusicPlayer().mediaPlayer!!.isPlaying) { binding.pause.setImageResource(R.drawable.pause1) }
                    else { binding.pause.setImageResource(R.drawable.play1)

                    }
                }
                Handler().postDelayed(this, 100)
            }
        })

        CoroutineScope(Dispatchers.Main).launch {


        binding.down.setOnClickListener{
//            MyMusicActivity.binding?.MPFRR?.visibility = View.INVISIBLE
//            MyMusicActivity.binding?.nowPlaying!!.visibility = View.VISIBLE
//            MyMusicActivity.binding?.navCL?.visibility = View.VISIBLE
//            MyMusicActivity.binding?.mainLayout?.visibility = View.VISIBLE
//            MyMusicActivity.binding?.barLL?.visibility = View.VISIBLE
            MyMusicActivity().onBackPressed()
        }

        binding.queue.setOnClickListener {
            val intent = Intent(requireContext(),QueueListActivity::class.java)
            intent.putExtra("currentSong",MusicPlayer.currentSong)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

       binding.moreBtn.setOnClickListener {
            val intent1 = Intent(requireContext(),MoreActivity::class.java)
            intent1.putExtra("AudioModel",MusicPlayer.currentSong)
            startActivity(intent1)
        }



            binding.share.setOnClickListener {
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(MusicPlayer.currentSong.path),
                    null
                ) { _, uri ->
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    shareIntent.type = "music/mp3"
                    startActivity(Intent.createChooser(shareIntent, "Share"))
                }
            }

        }
        return view
      }

        companion object{
            lateinit var binding: FragmentMusicPlayerBinding
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        MyMusicActivity().runOnUiThread(object : Runnable {

            override fun run() {
                if (MusicPlayer().mediaPlayer != null) {

                    binding.seekBar.progress = (MusicPlayer().mediaPlayer!!.currentPosition)
                    MusicFragment.binding.songProgress.progress = (MusicPlayer().mediaPlayer!!.currentPosition)

                    binding.currentTime.text = convertToMMSS(MusicPlayer().mediaPlayer!!.currentPosition.toString()+"")
                    if (MusicPlayer().mediaPlayer!!.isPlaying) {
                        binding.pause.setImageResource(R.drawable.pause1)


                    } else {
                        binding.pause.setImageResource(R.drawable.play1)


                    }
                }
                Handler().postDelayed(this, 100)
            }
        })
        binding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (MusicPlayer().mediaPlayer != null && b) {
                    MusicPlayer().mediaPlayer!!.seekTo(i)
                    MusicPlayer.musicService?.showNotification(context!!,MyMusicActivity.List,if (MusicPlayer().mediaPlayer!!.isPlaying) R.drawable.notify_pause
                    else R.drawable.notify_play,MyMediaPlayer.currentIndex,MusicPlayer().likesong(context!!,MyMusicActivity.List,MyMediaPlayer.currentIndex))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.down.setOnClickListener{
           MyMusicActivity().onBackPressed()
        }

        binding.queue.setOnClickListener {
            val intent = Intent(requireContext(),QueueListActivity::class.java)
            intent.putExtra("currentSong",MusicPlayer.currentSong)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)
        }

        binding.moreBtn.setOnClickListener {
            val intent1 = Intent(requireContext(),MoreActivity::class.java)
            intent1.putExtra("AudioModel",MusicPlayer.currentSong)
            startActivity(intent1)
        }


//        binding.likeBtn.setOnClickListener {
//            val dbHandler = LikedSongsDatabase(requireContext())
//            if (dbHandler.getLikedSongsList().contains( MusicPlayer.currentSong)){
//                binding.likeBtn.setImageResource(R.drawable.unliked)
//                Snackbar.make(requireView().findViewById(R.id.rrLayout),
//                    "Removed from Liked Songs",1000).show()
//                dbHandler.removeLikedSong(MusicPlayer.currentSong)
//                if (MusicPlayer().mediaPlayer!!.isPlaying){
//                    MusicPlayer.musicService?.showNotification(requireContext(),MyMusicActivity.List,R.drawable.notify_pause,MyMediaPlayer.currentIndex,
//                        MusicPlayer().likesong(requireContext(),MyMusicActivity.List,MyMediaPlayer.currentIndex))
//                }else{
//                    MusicPlayer.musicService?.showNotification(requireContext(),MyMusicActivity.List,R.drawable.notify_play,MyMediaPlayer.currentIndex,
//                        MusicPlayer().likesong(requireContext(),MyMusicActivity.List,MyMediaPlayer.currentIndex))
//                }
//
//            }
//            else {
//                binding.likeBtn.setImageResource(R.drawable.liked)
//                Snackbar.make(requireView().findViewById(R.id.rrLayout),
//                    "Added to Liked Songs",1000).show()
//                dbHandler.addLikedSong(MusicPlayer.currentSong)
//                if (MusicPlayer().mediaPlayer!!.isPlaying){
//                    MusicPlayer.musicService?.showNotification(requireContext(),MyMusicActivity.List,R.drawable.notify_pause,MyMediaPlayer.currentIndex,
//                        MusicPlayer().likesong(requireContext(),MyMusicActivity.List,MyMediaPlayer.currentIndex))
//                }else{
//                    MusicPlayer.musicService?.showNotification(requireContext(),MyMusicActivity.List,R.drawable.notify_play,MyMediaPlayer.currentIndex,
//                        MusicPlayer().likesong(requireContext(),MyMusicActivity.List,MyMediaPlayer.currentIndex))
//                }
//                MyMusicActivity.binding?.recyclerView?.adapter = LikedSongsAdapter(requireContext(),dbHandler.getLikedSongsList())
//
//            }
//            MyMusicActivity().refreshLikedList()
//        }

        binding.share.setOnClickListener {
            MediaScannerConnection.scanFile(requireContext(), arrayOf(MusicPlayer.currentSong.path),null){
                    _, uri->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                shareIntent.type = "music/mp3"
                startActivity(Intent.createChooser(shareIntent,"Share"))
            }
        }

//        binding.shuffle.setOnClickListener {
//            if (!shuffleMode){
//                binding.shuffle.setImageResource(R.drawable.greenshuffle)
//                shuffleMode = true
//                Snackbar.make(requireView().findViewById(R.id.rrLayout),
//                    "Shuffle is On",1000).show()
//            }
//            else {
//                binding.shuffle.setImageResource(R.drawable.shuffle)
//                Snackbar.make(requireView().findViewById(R.id.rrLayout),
//                    "Shuffle is Off",1000).show()
//                shuffleMode = false
//
//            }
//        }
    }
    }


fun convertToMMSS(duration: String): String {
        val millis = duration.toLong()
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )}
