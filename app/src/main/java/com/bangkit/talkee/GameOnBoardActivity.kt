package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.talkee.databinding.ActivityGameOnBoardBinding

class GameOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val state = intent.getStringExtra("STATE") ?: "NULL"
        val title = intent.getStringExtra("TITLE")
        binding.gameTitle.text = title

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
            }
        })

        binding.btnStart.setOnClickListener {
            val i = when(title) {
                "Game 1" -> Intent(this@GameOnBoardActivity, GameWordActivity::class.java)
                "Game 2" -> Intent(this@GameOnBoardActivity, GameSignActivity::class.java)
                else -> {
                    Intent(this@GameOnBoardActivity, GameCameraActivity::class.java)
                }
            }

            i.putExtra("TITLE", title)
            startActivity(i)
            finish()
        }

        binding.btnClose.setOnClickListener { finish() }

        if(state == "STATE_END") {
            val points = intent.getIntExtra("POINTS", 0)
            val resultCorrect = intent.getStringExtra("RESULT")

            val pointText = "+$points poin"
            val resultText = "$resultCorrect benar"
            binding.gameTextBox.visibility = View.GONE
            binding.resultBox.visibility = View.VISIBLE
            binding.gameResultPoint.text = pointText
            binding.gameResultAnswer.text = resultText
            binding.btnStart.text = "Kembali"

            binding.btnStart.setOnClickListener { finish() }
        }
    }

    private fun setGameTextBox(screenWidth: Int) {
        val marginInDp = 20
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val availableWidth = screenWidth - 2 * marginInPx
        val boxHeight = availableWidth * 4 / 3

        val layoutParams = binding.gameTextBox.layoutParams
        layoutParams.width = availableWidth
        layoutParams.height = boxHeight
        binding.gameTextBox.layoutParams = layoutParams
        binding.resultBox.layoutParams = layoutParams
        binding.btnStart.layoutParams.width = layoutParams.width
    }
}