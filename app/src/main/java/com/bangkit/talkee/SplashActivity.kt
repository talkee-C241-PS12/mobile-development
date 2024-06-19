package com.bangkit.talkee

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.databinding.ActivitySplashBinding

private const val ANIMATION_DELAY_START = 1000
private const val SPLASH_DURATION = 2000
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.white)

        Handler(Looper.getMainLooper()).postDelayed({
            playAnimation()
            exitSplash()
        }, ANIMATION_DELAY_START.toLong())
    }
    private fun playAnimation() {
        val upAnim = ObjectAnimator.ofFloat(binding.imageSplash, View.TRANSLATION_Y, 0f, -100f).apply {
            duration = 500
        }

        val downAnim = ObjectAnimator.ofFloat(binding.imageSplash, View.TRANSLATION_Y, -100f, 10000f).apply {
            duration = 1500
        }

        AnimatorSet().apply {
            play(upAnim).before(downAnim)
            start()
        }
    }

    private fun exitSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this@SplashActivity, OnBoardActivity::class.java)
            startActivity(i)
            finish()
        }, SPLASH_DURATION.toLong())
    }
}