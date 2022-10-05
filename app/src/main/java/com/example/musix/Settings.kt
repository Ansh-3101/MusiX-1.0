package com.example.musix

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musix.databinding.ActivitySettingsBinding
import com.google.android.material.snackbar.Snackbar

class Settings : AppCompatActivity() {
    private var binding : ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.equalizer?.setOnClickListener {
            val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            if ((intent.resolveActivity(packageManager) != null)) {

                startActivityForResult(intent,1)
            }
            else{
                Snackbar.make(binding?.root!!,"No Equalizer found",1000).show()
            }
        }

    }
}