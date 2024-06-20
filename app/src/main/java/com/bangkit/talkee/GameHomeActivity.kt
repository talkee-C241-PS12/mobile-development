package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit.talkee.adapter.GameHomeAdapter
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.GameRepository
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.response.GameItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.GameViewModel
import com.bangkit.talkee.data.viewmodel.GameViewModelFactory
import com.bangkit.talkee.data.viewmodel.UserViewModel
import com.bangkit.talkee.data.viewmodel.UserViewModelFactory
import com.bangkit.talkee.databinding.ActivityGameHomeBinding
import com.bangkit.talkee.utils.formatNumber
import com.bumptech.glide.Glide
import kotlin.math.roundToInt

class GameHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameHomeBinding
    private lateinit var gameHomeAdapter: GameHomeAdapter
    private lateinit var gameViewModel: GameViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading(true)

        initViewModel()
        initUserViewModel()

        gameHomeAdapter = GameHomeAdapter(null)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = gameHomeAdapter

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = GameRepository(apiService)
        val kelasViewModelFactory = GameViewModelFactory(repo)
        gameViewModel = ViewModelProvider(this, kelasViewModelFactory)[GameViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        gameViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                gameViewModel.clearErrorMessage()
            }
        }

        gameViewModel.successMessage.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                gameViewModel.clearSuccessMessage()
            }
        }

        gameViewModel.gameResponse.observe(this) { allGames ->
            isLoading(false)
            if (!(allGames.data.isNullOrEmpty())) {
                val adapter = GameHomeAdapter(allGames)

                adapter.setOnItemClickCallback(object : GameHomeAdapter.OnItemClickCallback {
                    override fun onItemClicked(game: GameItem) {
                        val i = Intent(this@GameHomeActivity, GameOnBoardActivity::class.java)
                        i.putExtra(GameOnBoardActivity.SELECTED_ITEM, game)
                        startActivity(i)
                    }
                })
                binding.recyclerView.adapter = adapter
            } else {
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        getAllGames()
    }

    private fun getAllGames() {
        isLoading(true)
        gameViewModel.getAllGames()
    }

    private fun initUserViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = UserRepository(apiService)
        val userViewModelFactory = UserViewModelFactory(repo)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        initUserObserver()
    }

    private fun initUserObserver() {
        userViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                userViewModel.clearErrorMessage()
            }
        }

        userViewModel.profileResponse.observe(this) { profileResponse ->
            if(profileResponse.nama != null) {
                val userName = profileResponse.nama.toString()
                val userPhotoUrl = profileResponse.image.toString()
                val userLeaderboard = profileResponse.leaderboard
                val userPoints = profileResponse.poin

                Glide.with(binding.userProfilePic.context).load(userPhotoUrl).into(binding.userProfilePic)
                binding.txtUserName.text = userName
                binding.txtUserPoints.text = formatNumber(userPoints?.roundToInt() ?: 0)
                binding.txtUserRank.text = userLeaderboard.toString()

                when(userLeaderboard) {
                    1 -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_first)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_first))
                    }
                    2 -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_second)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_second))
                    }
                    else -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_blank)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_default))
                    }
                }

                showUserProfile(true)
            }
        }

        getUserProfile()
    }

    private fun getUserProfile() {
        val tokenManager = TokenManager(this)
        val idToken = tokenManager.getIDToken()

        if (idToken != null) {
            try {
                userViewModel.getUserProfile(idToken)
            } catch (e: Exception) {
                showToast("Gagal mendapatkan profil.")
            }
        }
    }

    private fun showUserProfile(show: Boolean) {
        if(show) {
            binding.progressCircularLeaderboard.visibility = View.GONE
            binding.leaderboard.visibility = View.VISIBLE
        } else {
            binding.progressCircularLeaderboard.visibility = View.VISIBLE
            binding.leaderboard.visibility = View.INVISIBLE
        }
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
}