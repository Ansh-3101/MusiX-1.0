package com.example.musix

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.musix.databinding.FragmentMusicBinding


class MusicFragment : Fragment(),ServiceConnection
{
    lateinit var list :ArrayList<AudioModel>


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)
        binding = FragmentMusicBinding.bind(view)

        if (MusicPlayer().mediaPlayer != null) {
            if (MusicPlayer().mediaPlayer?.isPlaying == true) {
                binding.playPauseBtnNP.setImageResource(R.drawable.pause1)
            } else {
                binding.playPauseBtnNP.setImageResource(R.drawable.play1)
            }
        }
        else{
            binding.root.visibility = View.VISIBLE
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        val intent = Intent(requireContext(),MusicService::class.java)
        requireContext().bindService(intent,this, BIND_AUTO_CREATE)
        requireContext().startService(intent)

        binding.root.setOnClickListener{

           MyMusicActivity.binding?.MPFRR?.visibility = View.VISIBLE
           MyMusicActivity.binding?.PLFRR?.visibility = View.INVISIBLE
            MyMusicActivity.binding?.nav?.visibility = View.INVISIBLE
            MyMusicActivity.binding?.barLL?.visibility = View.INVISIBLE
            MyMusicActivity.binding?.mainLayout?.visibility = View.INVISIBLE
        }

            if (MusicPlayer().mediaPlayer?.isPlaying == true) {
                binding.playPauseBtnNP.setImageResource(R.drawable.pause1)
            }
            else {
                binding.playPauseBtnNP.setImageResource(R.drawable.play1)
            }
    }



    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding : FragmentMusicBinding
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        MusicPlayer.musicService = binder.currentService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onServiceDisconnected(p0: ComponentName?) {
        MusicPlayer.musicService = null
    }
}