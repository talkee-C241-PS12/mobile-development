package com.bangkit.talkee.controller

import android.view.View
import com.bangkit.talkee.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer


internal class YoutubeCustomUiController(customPlayerUI: View, youTubePlayer: YouTubePlayer) {
    private var isPlaying = false

    init {
        val playPauseButton = customPlayerUI.findViewById<View>(R.id.play_pause_button)
        playPauseButton.setOnClickListener {
            if (isPlaying) youTubePlayer.pause() else youTubePlayer.play()
            isPlaying = !isPlaying
        }
    }
}