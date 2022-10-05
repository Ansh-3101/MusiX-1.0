package com.example.musix

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private var timer : CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val view = findViewById<ConstraintLayout>(R.id.main)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : PermissionListener, MultiplePermissionsListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse?) {}
                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse?) {
                    Snackbar.make(view,"App wont without these permissions",1000).show()
                    Snackbar.make(view,"Exiting Musix",1000).show()
                    CoroutineScope(Dispatchers.IO).launch{
                        delay(1000)
                         exitProcess(1)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?,
                    permissionToken: PermissionToken?)
                { permissionToken?.continuePermissionRequest() }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()){

                        var barProgress = 0
                        val progressBar : ProgressBar =  findViewById(R.id.progressBar)
                        timer = object : CountDownTimer(2000,27){
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onTick(p0: Long) {
                                barProgress++
                                progressBar.setProgress(barProgress,true)
                            }

                            override fun onFinish() {
                                CoroutineScope(Dispatchers.IO).launch {
                                val intent  = Intent(applicationContext,MyMusicActivity::class.java)
                                startActivity(intent)
                                finish()
                                }
                            }

                        }.start()
                    }
                    else{
                       Snackbar.make(view,"Required permissions not granted",1000).show()
                        Snackbar.make(view,"Exiting Musix",1000).show()
                        CoroutineScope(Dispatchers.IO).launch{
                            delay(1000)
                            exitProcess(1)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?) {}
            }).check()





    }

}