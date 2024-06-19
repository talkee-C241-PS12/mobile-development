package com.bangkit.talkee

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.GameRepository
import com.bangkit.talkee.data.response.PertanyaanItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.GameViewModel
import com.bangkit.talkee.data.viewmodel.GameViewModelFactory
import com.bangkit.talkee.databinding.ActivityGameSignBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment
import com.bangkit.talkee.utils.getFixedResources
import kotlin.math.roundToInt

class GameSignActivity : AppCompatActivity(), ExitDialogFragment.ExitDialogListener {

    private lateinit var binding: ActivityGameSignBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var userIdToken: String
    private var currentQuestionIndex = 0
    private var score = 0
    private var maxPoints = 0.0

    private lateinit var questions: MutableList<MutableList<String?>>
    private lateinit var questionsId: MutableList<String?>
    private lateinit var answers: MutableList<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserIdToken()
        initViewModel()

        val pertanyaanList: ArrayList<PertanyaanItem>? = if(Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableArrayListExtra(GameOnBoardActivity.SELECTED_ITEM, PertanyaanItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(GameOnBoardActivity.SELECTED_ITEM)
        }
        val gameTitle = intent.getStringExtra(GameOnBoardActivity.GAME_TITLE)
        val gameMaxPoints = intent.getDoubleExtra(GameOnBoardActivity.GAME_MAX_POINTS, 0.0)

        binding.gameTitle.text = gameTitle
        maxPoints = gameMaxPoints

        if(pertanyaanList != null) {
            val questionsBuff: MutableList<MutableList<String?>> = mutableListOf()
            val questionsIdBuff: MutableList<String?> = mutableListOf()
            val answersBuff: MutableList<String?> = mutableListOf()

            for(pertanyaan in pertanyaanList) {
                val choiceBuff: MutableList<String?> = mutableListOf()
                choiceBuff.add(pertanyaan.jawaban1)
                choiceBuff.add(pertanyaan.jawaban2)
                choiceBuff.add(pertanyaan.jawaban3)

                answersBuff.add(pertanyaan.jawaban)
                questionsIdBuff.add(pertanyaan.idpertanyaan)
                questionsBuff.add(choiceBuff)
            }

            questions = questionsBuff
            questionsId = questionsIdBuff
            answers = answersBuff
        } else {
            questions = mutableListOf()
            questionsId = mutableListOf()
            answers = mutableListOf()
        }

        val gameQuestionText = "${getString(R.string.question_game_sign)} (${(maxPoints / questions.size.toDouble()).roundToInt()} poin)"
        binding.questionText.text = gameQuestionText

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

    private fun getUserIdToken() {
        val tokenManager = TokenManager(this)
        val idToken = tokenManager.getIDToken()
        if (idToken != null) {
            userIdToken = idToken
        }
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
                val userAnswer = questions[currentQuestionIndex][i]

                answerQuestion(userIdToken, questionsId[currentQuestionIndex]!!, userAnswer!!)

                if(userAnswer == answers[currentQuestionIndex]) {
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
                        val pointsUserGained = (score.toDouble() / questions.size.toDouble()) * maxPoints

                        val intentResult = Intent(this@GameSignActivity, GameOnBoardActivity::class.java)
                        intentResult.putExtra(GameOnBoardActivity.GAME_TITLE, binding.gameTitle.text.toString())
                        intentResult.putExtra(GameOnBoardActivity.STATE, "STATE_END")
                        intentResult.putExtra(GameOnBoardActivity.GAME_USER_SCORE, pointsUserGained.roundToInt())
                        intentResult.putExtra(GameOnBoardActivity.GAME_USER_RESULT, result)
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
            val resource = getFixedResources(questions[currentQuestionIndex][i]!!)
            choiceImages[i].setImageResource(resource ?: R.drawable.img_bg_game)
        }
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = GameRepository(apiService)
        val gameViewModelFactory = GameViewModelFactory(repo)
        gameViewModel = ViewModelProvider(this, gameViewModelFactory)[GameViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        gameViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                gameViewModel.clearErrorMessage()
            }
        }

        gameViewModel.gameAnswerResponse.observe(this) { gameAnswerResponse ->
            if(gameAnswerResponse.hasil != null) {
                Log.d("ANSWER", gameAnswerResponse.hasil.toString())
            }
        }
    }

    private fun answerQuestion(token: String, idpertanyaan: String, jawaban: String) {
        gameViewModel.answerQuestion(token, idpertanyaan, jawaban)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialogChoice(isCorrect: Boolean) {
        if(isCorrect) {
            val color = ContextCompat.getColor(this, R.color.correct)
            val pointText = "+${(maxPoints / questions.size.toDouble()).roundToInt()} poin"
            binding.dialogIcon.setImageResource(R.drawable.ic_check)
            binding.dialogIcon.setColorFilter(color)
            binding.dialogText.text = getString(R.string.answer_correct)
            binding.dialogPoint.text = pointText
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