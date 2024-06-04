package com.bangkit.talkee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.databinding.ActivityLearnVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class LearnVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnVideoBinding
    private lateinit var youTubePlayer: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        youTubePlayer = binding.ytPlayer

        lifecycle.addObserver(youTubePlayer)

        youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }
}