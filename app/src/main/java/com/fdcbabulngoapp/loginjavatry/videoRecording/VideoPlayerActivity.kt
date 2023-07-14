package com.fdcbabulngoapp.loginjavatry.videoRecording
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.fdcbabulngoapp.loginjavatry.R

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.video_View)
        val videoUri = intent.getStringExtra("videoUri")
        Log.d("VideoPlayerActivity", "Video URI: $videoUri")
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        val uri = Uri.parse(videoUri)
        Log.d("VideoPlayerActivity", "Video URI: ${uri.toString()}")
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }
}


