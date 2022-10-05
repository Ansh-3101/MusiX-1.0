package com.example.musix


import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musix.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private var binding:ActivityAboutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backBtn?.setOnClickListener{super.onBackPressed()}
       binding?.aboutMail?.setOnClickListener {goToGmail()}
        binding?.gmail?.setOnClickListener{goToGmail()}

        binding?.github?.setOnClickListener { goToGitHub() }
        binding?.githubImg?.setOnClickListener { goToGitHub() }
//        binding?.githubInfo?.setOnClickListener { goToGitHub() }


    }
    fun goToGmail(){
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "apsingh3101@gmail.com",null))
            this.startActivity(Intent.createChooser(intent,null))
    }
    fun goToGitHub(){
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY,"github.com/Ansh-3101/")
        this.startActivity(intent)
    }
}