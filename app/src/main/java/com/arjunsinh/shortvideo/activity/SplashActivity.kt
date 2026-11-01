package com.arjunsinh.shortvideo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arjunsinh.shortvideo.R
import com.arjunsinh.shortvideo.databinding.ActivitySplashBinding
import com.arjunsinh.shortvideo.roomDatabase.Callback
import com.arjunsinh.shortvideo.roomDatabase.VideoModel
import com.arjunsinh.shortvideo.roomDatabase.ViewModel

class SplashActivity : AppCompatActivity() {

    lateinit var viewModal: ViewModel
    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inIt()
        setClick()
    }

    private fun inIt() {
        viewModal = ViewModel(application);

        getAllVideos()
    }

    private fun setClick() {

    }
    fun getAllVideos(){
        viewModal.getAllVideo(object : Callback {
            override fun onVideoCallBack(list: MutableList<VideoModel>) {
                finalStep(list)
            }
            override fun onVideoCallBack1(list: List<VideoModel>) {

            }
        })
    }
    fun finalStep(list: MutableList<VideoModel>) {
        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (list.size>0){
                var intent = Intent(this@SplashActivity, VideoPlayerActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                var intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        },1000)
    }
}