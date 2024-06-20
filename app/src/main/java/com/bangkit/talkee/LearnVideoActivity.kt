package com.bangkit.talkee

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.data.response.KelasDetailItem
import com.bangkit.talkee.databinding.ActivityLearnVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class LearnVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnVideoBinding
    private lateinit var youTubePlayer: YouTubePlayerView
    private lateinit var kelasDetailItem: KelasDetailItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading(true)

        val detailKelasItem = if(Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(SELECTED_ITEM, KelasDetailItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(SELECTED_ITEM)
        }

        if (detailKelasItem != null) {
            kelasDetailItem = detailKelasItem
            binding.wordName.text = kelasDetailItem.kata
        }

        youTubePlayer = binding.ytPlayer

        lifecycle.addObserver(youTubePlayer)

        youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                isLoading(false)
                val videoId = kelasDetailItem.video
                if (videoId != null) {
                    youTubePlayer.loadVideo(videoId, 0f)
                } else {
                    binding.tvFailedToLoad.visibility = View.VISIBLE
                }
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

    @SuppressLint("SourceLockedOrientationActivity")
    private fun enterFullscreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        youTubePlayer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer.release()
    }

    private fun isLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val SELECTED_ITEM = "SELECTED_VIDEO"
    }
}