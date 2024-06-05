package com.bangkit.talkee

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.databinding.ActivityLearnVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
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

        val customPlayerUi = youTubePlayer.inflateCustomPlayerUi(R.layout.custom_player_ui)

        youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                val videoId = "6nS7tt4Ovjg"
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                if(state == PlayerConstants.PlayerState.ENDED){
                    youTubePlayer.seekTo(0f)
                    youTubePlayer.play()
                }
            }
        })

        enterFullscreen()

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun enterFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        youTubePlayer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer.release()
    }
}