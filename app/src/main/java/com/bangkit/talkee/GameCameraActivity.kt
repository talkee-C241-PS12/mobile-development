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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.GameRepository
import com.bangkit.talkee.data.repository.ModelMLRepository
import com.bangkit.talkee.data.response.PertanyaanItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.GameViewModel
import com.bangkit.talkee.data.viewmodel.GameViewModelFactory
import com.bangkit.talkee.data.viewmodel.ModelMLViewModel
import com.bangkit.talkee.data.viewmodel.ModelMLViewModelFactory
import com.bangkit.talkee.databinding.ActivityGameCameraBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment
import java.io.File
import kotlin.math.roundToInt

class GameCameraActivity : AppCompatActivity(), ExitDialogFragment.ExitDialogListener {

    private lateinit var binding: ActivityGameCameraBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var modelMLViewModel: ModelMLViewModel
    private lateinit var userIdToken: String
    private var currentQuestionIndex = 0
    private var score = 0
    private var maxPoints: Double = 0.0

    private lateinit var questionsId: MutableList<String?>
    private lateinit var answers: MutableList<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
                setButtonCamera(screenWidth)
            }
        })

        getUserIdToken()
        initViewModel()
        initViewModelML()

        val pertanyaanList: ArrayList<PertanyaanItem>? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableArrayListExtra(GameOnBoardActivity.SELECTED_ITEM, PertanyaanItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(GameOnBoardActivity.SELECTED_ITEM)
        }

        val gameTitle = intent.getStringExtra(GameOnBoardActivity.GAME_TITLE)
        val gameMaxPoints = intent.getDoubleExtra(GameOnBoardActivity.GAME_MAX_POINTS, 0.0)

        binding.gameTitle.text = gameTitle
        maxPoints = gameMaxPoints

        if (pertanyaanList != null) {
            val questionsIdBuff: MutableList<String?> = mutableListOf()
            val answersBuff: MutableList<String?> = mutableListOf()

            for (pertanyaan in pertanyaanList) {
                answersBuff.add(pertanyaan.jawaban)
                questionsIdBuff.add(pertanyaan.idpertanyaan)
            }

            questionsId = questionsIdBuff
            answers = answersBuff
        } else {
            questionsId = mutableListOf()
            answers = mutableListOf()
        }

        val gameQuestionText = "${getString(R.string.question_game_camera)} (${(maxPoints / answers.size.toDouble()).roundToInt()} poin)"
        binding.questionText.text = gameQuestionText

        updateProgress()
        updateQuestion()

        binding.btnClose.setOnClickListener { showExitDialog() }
    }

    private fun getUserIdToken() {
        val tokenManager = TokenManager(this)
        val idToken = tokenManager.getIDToken()
        if (idToken != null) {
            userIdToken = idToken
        }
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService(ApiConfig.ROUTE_BASE_URL)
        val repo = GameRepository(apiService)
        val gameViewModelFactory = GameViewModelFactory(repo)
        gameViewModel = ViewModelProvider(this, gameViewModelFactory)[GameViewModel::class.java]

        initObserver()
    }

    private fun initViewModelML() {
        val apiServiceML = ApiConfig.getApiService(ApiConfig.ROUTE_ML_MODEL)
        val repoML = ModelMLRepository(apiServiceML)
        val modelMLViewModelFactory = ModelMLViewModelFactory(repoML)
        modelMLViewModel = ViewModelProvider(this, modelMLViewModelFactory)[ModelMLViewModel::class.java]

        initObserverML()
    }

    private fun initObserver() {
        gameViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                gameViewModel.clearErrorMessage()
            }
        }

        gameViewModel.gameAnswerResponse.observe(this) { gameAnswerResponse ->
            gameAnswerResponse.hasil?.let {
                Log.d("ANSWER", it.toString())
            }
        }
    }

    private fun initObserverML() {
        modelMLViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                modelMLViewModel.clearErrorMessage()
            }
        }

        modelMLViewModel.modelMLResponse.observe(this) { modelMLResponse ->
            var modelPrediction = "c"
            modelMLResponse.message?.let { message ->
                Log.d("RESPONSE", message)
                modelPrediction = message.lowercase()
            }

            answerQuestion(userIdToken, questionsId[currentQuestionIndex]!!, modelPrediction)
            verifyAnswer(modelPrediction)

            binding.btnCamera.visibility = View.VISIBLE
            binding.progressCircularCamera.visibility = View.GONE
        }
    }

    private fun uploadVideoToModel(videoFile: File) {
        binding.btnCamera.visibility = View.INVISIBLE
        binding.progressCircularCamera.visibility = View.VISIBLE
        modelMLViewModel.uploadVideoAnswer(videoFile)
    }

    private fun answerQuestion(token: String, idpertanyaan: String, jawaban: String) {
        gameViewModel.answerQuestion(token, idpertanyaan, jawaban)
    }

    private fun verifyAnswer(answer: String) {
        if (answer == answers[currentQuestionIndex]) {
            updateScore()
            showDialogChoice(true)
        } else {
            showDialogChoice(false)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.dialogChoice.visibility = View.GONE
            currentQuestionIndex++

            if (currentQuestionIndex < answers.size) {
                updateProgress()
                updateQuestion()
            } else {
                val result = "$score / ${answers.size}"
                val pointsUserGained = (score.toDouble() / answers.size.toDouble()) * maxPoints

                val intentResult = Intent(this@GameCameraActivity, GameOnBoardActivity::class.java).apply {
                    putExtra(GameOnBoardActivity.GAME_TITLE, binding.gameTitle.text.toString())
                    putExtra(GameOnBoardActivity.STATE, "STATE_END")
                    putExtra(GameOnBoardActivity.GAME_USER_SCORE, pointsUserGained.roundToInt())
                    putExtra(GameOnBoardActivity.GAME_USER_RESULT, result)
                }
                startActivity(intentResult)
                finish()
            }
        }, 2000)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

        binding.questionBox.layoutParams = binding.questionBox.layoutParams.apply {
            width = availableWidth
            height = boxHeight
        }
    }

    private fun setButtonCamera(screenWidth: Int) {
        val marginInDp = 20
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val availableWidth = screenWidth - 2 * marginInPx

        binding.btnCamera.layoutParams = binding.btnCamera.layoutParams.apply {
            width = availableWidth
        }

        binding.btnCamera.setOnClickListener {
            val intent = Intent(this@GameCameraActivity, RecordVideoActivity::class.java)
            videoResultLauncher.launch(intent)
        }
    }

    private val videoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val videoUri = result.data?.data
            videoUri?.let {
                val videoFile = File(it.path ?: return@let)
                uploadVideoToModel(videoFile)
            }
        }
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

    private fun showDialogChoice(isCorrect: Boolean) {
        if (isCorrect) {
            val color = ContextCompat.getColor(this, R.color.correct)
            val pointText = "+${(maxPoints / answers.size.toDouble()).roundToInt()} poin"
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
}
