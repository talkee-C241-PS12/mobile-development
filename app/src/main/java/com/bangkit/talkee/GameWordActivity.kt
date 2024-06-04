package com.bangkit.talkee

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.talkee.databinding.ActivityGameWordBinding

class GameWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameWordBinding
    private var currentQuestionIndex = 0
    private var score = 0

    private val questions = listOf(
        listOf("A", "B", "C"),
        listOf("D", "E", "F"),
        listOf("A", "C", "G"),
        listOf("D", "M", "C"),
        listOf("Z", "X", "C"),
        listOf("A", "J", "L"),
        listOf("P", "O", "X"),
        listOf("C", "D", "J"),
        listOf("I", "O", "A"),
    )
    private val answers = listOf("B", "E", "A", "C", "X", "J", "P", "C", "I")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("TITLE")
        binding.gameTitle.text = title

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
                setWordChoiceBox(screenWidth)
            }
        })

        updateProgress()
        updateQuestion()
    }

    private fun updateProgress() {
        binding.progressBar.progress = (currentQuestionIndex * 100) / questions.size
    }

    private fun updateQuestion() {
        binding.choice1.text = questions[currentQuestionIndex][0]
        binding.choice2.text = questions[currentQuestionIndex][1]
        binding.choice3.text = questions[currentQuestionIndex][2]
    }

    private fun updateScore() {
        score++
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
    }

    private fun setWordChoiceBox(screenWidth: Int) {
        val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
        val choiceSize = screenWidth / 3

        choices.forEach { choice ->
            val layoutParams = choice.layoutParams
            layoutParams.width = choiceSize
            layoutParams.height = choiceSize - (12 * 4)
            choice.layoutParams = layoutParams

            choice.setOnClickListener { it ->
                it.isSelected = true
                (it as TextView).setTextColor(getColor(R.color.white))

                if(it.text == answers[currentQuestionIndex]) {
                    updateScore()
                    showDialogChoice(true)
                } else {
                    showDialogChoice(false)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.dialogChoice.visibility = View.GONE
                    currentQuestionIndex++

                    if(currentQuestionIndex < questions.size) {
                        updateProgress()
                        updateQuestion()

                        choices.forEach {it2 ->
                            it2.isSelected = false
                            it2.setTextColor(getColor(R.color.black))
                        }
                    } else {
                        val result = "$score / ${questions.size}"
                        val i = Intent(this@GameWordActivity, GameOnBoardActivity::class.java)
                        i.putExtra("TITLE", title)
                        i.putExtra("STATE", "STATE_END")
                        i.putExtra("POINTS", score * 100)
                        i.putExtra("RESULT", result)
                        startActivity(i)
                        finish()
                    }
                }, 2000)
            }
        }
    }

    private fun showDialogChoice(isCorrect: Boolean) {
        if(isCorrect) {
            val color = ContextCompat.getColor(this, R.color.correct)
            binding.dialogIcon.setImageResource(R.drawable.ic_check)
            binding.dialogIcon.setColorFilter(color)
            binding.dialogText.text = getString(R.string.answer_correct)
            binding.dialogPoint.visibility = View.VISIBLE
        } else {
            val color = ContextCompat.getColor(this, R.color.error)
            binding.dialogIcon.setImageResource(R.drawable.ic_close)
            binding.dialogIcon.setColorFilter(color)
            binding.dialogText.text = getString(R.string.answer_wrong)
            binding.dialogPoint.visibility = View.GONE
        }
        binding.dialogChoice.visibility = View.VISIBLE
    }
}