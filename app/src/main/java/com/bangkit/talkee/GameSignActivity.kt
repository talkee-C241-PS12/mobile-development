package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.talkee.databinding.ActivityGameSignBinding
import com.bangkit.talkee.databinding.ActivityGameWordBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment

class GameSignActivity : AppCompatActivity(), ExitDialogFragment.ExitDialogListener {

    private lateinit var binding: ActivityGameSignBinding

    private var currentQuestionIndex = 0
    private var score = 0

    private val questions = listOf(
        listOf("A", "D", "O"),
        listOf("D", "O", "A"),
        listOf("O", "D", "A"),
        listOf("O", "A", "D"),
        listOf("A", "O", "D"),
        listOf("D", "A", "O"),
    )
    private val answers = listOf("A", "D", "A", "O", "D", "A")

    private val fixedResources: Map<String, Int> = mapOf(
        "A" to R.drawable.hand_a,
        "D" to R.drawable.hand_d,
        "O" to R.drawable.hand_o
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("TITLE")
        binding.gameTitle.text = title

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
                setChoiceBox(screenWidth)
            }
        })

        updateProgress()
        updateQuestion()

        binding.btnClose.setOnClickListener { showExitDialog() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun updateProgress() {
        binding.progressBar.progress = (currentQuestionIndex * 100) / answers.size
    }

    private fun updateQuestion() {
        binding.questionBox.text = answers[currentQuestionIndex]
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

        val layoutParams = binding.questionBox.layoutParams
        layoutParams.width = availableWidth
        layoutParams.height = boxHeight
        binding.questionBox.layoutParams = layoutParams
    }

    private fun setChoiceBox(screenWidth: Int) {
        val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
        val choiceSelectors = listOf(binding.choiceSelector1, binding.choiceSelector2, binding.choiceSelector3)
        val choiceSize = screenWidth / 3

        setChoiceImage()
        for(i in 0..2) {
            val layoutParams = choices[i].layoutParams
            layoutParams.width = choiceSize
            layoutParams.height = choiceSize - (12 * 4)
            choices[i].layoutParams = layoutParams

            choices[i].setOnClickListener {
                choiceSelectors[i].visibility = View.VISIBLE

                if(questions[currentQuestionIndex][i] == answers[currentQuestionIndex]) {
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
                        setChoiceImage()

                        choiceSelectors.forEach {selectors ->
                            selectors.visibility = View.GONE
                        }
                    } else {
                        val result = "$score / ${questions.size}"
                        val intentResult = Intent(this@GameSignActivity, GameOnBoardActivity::class.java)
                        intentResult.putExtra("TITLE", title)
                        intentResult.putExtra("STATE", "STATE_END")
                        intentResult.putExtra("POINTS", score * 100)
                        intentResult.putExtra("RESULT", result)
                        startActivity(intentResult)
                        finish()
                    }
                }, 2000)
            }
        }
    }

    private fun setChoiceImage() {
        val choiceImages = listOf(binding.choiceImage1, binding.choiceImage2, binding.choiceImage3)

        for(i in 0..2) {
            val resource = fixedResources[questions[currentQuestionIndex][i]]
            choiceImages[i].setImageResource(resource ?: R.drawable.img_bg_game)
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

    override fun onExit() {
        finish()
    }

    private fun showExitDialog() {
        val exitDialog = ExitDialogFragment.newInstance(
            "Keluar",
            "Apakah kamu yakin ingin keluar? Game tidak akan disimpan.",
            "Keluar"
        )
        exitDialog.listener = this
        exitDialog.show(supportFragmentManager, "exitDialog")
    }
}