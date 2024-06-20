package com.bangkit.talkee

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.GameRepository
import com.bangkit.talkee.data.response.GameItem
import com.bangkit.talkee.data.response.PertanyaanItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.GameViewModel
import com.bangkit.talkee.data.viewmodel.GameViewModelFactory
import com.bangkit.talkee.databinding.ActivityGameOnBoardBinding

class GameOnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOnBoardBinding
    private lateinit var gameItem: GameItem
    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading(true)
        initViewModel()

        val state = intent.getStringExtra(STATE) ?: "NULL"

        if(state == "STATE_END") {
            isLoading(false)
            val points = intent.getIntExtra(GAME_USER_SCORE, 0)
            val resultCorrect = intent.getStringExtra(GAME_USER_RESULT)

            val pointText = "+$points poin"
            val resultText = "$resultCorrect benar"
            binding.gameTextBox.visibility = View.GONE
            binding.resultBox.visibility = View.VISIBLE
            binding.gameResultPoint.text = pointText
            binding.gameResultAnswer.text = resultText
            binding.btnStart.text = "Kembali"

            binding.btnStart.setOnClickListener { finish() }
        } else {
            val detailGame = if(Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(SELECTED_ITEM, GameItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(SELECTED_ITEM)
            }

            if (detailGame != null) {
                gameItem = detailGame
                binding.gameTitle.text = gameItem.nama
                getGameDetail(gameItem)
            }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
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

    private fun setGameButton(gameType: Int, gameTitle: String, gameMaxPoints: Double, pertanyaan: ArrayList<PertanyaanItem>) {
        binding.btnStart.setOnClickListener {
            val i = when(gameType) {
                0 -> Intent(this@GameOnBoardActivity, GameCameraActivity::class.java)
                1 -> Intent(this@GameOnBoardActivity, GameWordActivity::class.java)
                2 -> Intent(this@GameOnBoardActivity, GameSignActivity::class.java)
                else -> {
                    Intent(this@GameOnBoardActivity, GameCameraActivity::class.java)
                }
            }

            i.putExtra(GAME_TITLE, gameTitle)
            i.putExtra(GAME_MAX_POINTS, gameMaxPoints)
            i.putParcelableArrayListExtra(SELECTED_ITEM, pertanyaan)
            startActivity(i)
            finish()
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

        gameViewModel.gameDetailResponse.observe(this) { gameDetailResponse ->
            if(gameDetailResponse.nama != null) {
                val tips = gameDetailResponse.tips
                    ?.replace("\\n", "\n")
                    ?.replace("{poin}", gameDetailResponse.poin.toString())

                binding.gameTextBox.text = tips

                val tokenManager = TokenManager(this)
                val idToken = tokenManager.getIDToken()
                if (idToken != null && !(gameDetailResponse.idgame.isNullOrEmpty())) {
                    startGame(idToken, gameDetailResponse.idgame)
                }
            }
        }

        gameViewModel.gameStartResponse.observe(this) { gameStartResponse ->
            if(gameStartResponse.nama != null) {
                isLoading(false)

                if(gameStartResponse != null) {
                    setGameButton(gameStartResponse.pertanyaan[0].tipe, gameStartResponse.nama, gameStartResponse.poin!!, ArrayList(gameStartResponse.pertanyaan))
                }
                binding.btnStart.visibility = View.VISIBLE
            }
        }
    }

    private fun getGameDetail(item: GameItem) {
        item.idgame?.let { gameViewModel.getGameDetail(it) }
    }

    private fun startGame(token: String, idGame: String) {
        gameViewModel.startGame(token, idGame)
    }

    private fun isLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val STATE = "STATE"
        const val SELECTED_ITEM = "SELECTED_GAME"
        const val GAME_TITLE = "GAME_TITLE"
        const val GAME_MAX_POINTS = "GAME_MAX_POINTS"
        const val GAME_USER_SCORE = "GAME_USER_SCORE"
        const val GAME_USER_RESULT = "GAME_USER_RESULT"
    }
}