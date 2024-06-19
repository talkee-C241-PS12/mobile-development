package com.bangkit.talkee

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.LeaderboardAdapter
import com.bangkit.talkee.data.repository.LeaderboardRepository
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.LeaderboardViewModel
import com.bangkit.talkee.data.viewmodel.LeaderboardViewModelFactory
import com.bangkit.talkee.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var leaderboardViewModel: LeaderboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        leaderboardAdapter = LeaderboardAdapter(null)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = leaderboardAdapter

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = LeaderboardRepository(apiService)
        val leaderboardViewModelFactory = LeaderboardViewModelFactory(repo)
        leaderboardViewModel = ViewModelProvider(this, leaderboardViewModelFactory)[LeaderboardViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        leaderboardViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                leaderboardViewModel.clearErrorMessage()
            }
        }

        leaderboardViewModel.successMessage.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                leaderboardViewModel.clearSuccessMessage()
            }
        }

        leaderboardViewModel.leaderboardResponse.observe(this) { leaderboard ->
            isLoading(false)
            if (!(leaderboard.data.isNullOrEmpty())) {
                val adapter = LeaderboardAdapter(leaderboard)

                binding.recyclerView.adapter = adapter
            } else {
                binding.tvFailedToLoad.text = getString(R.string.failed_to_load_leaderboard)
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        getLeaderboard()
    }

    private fun getLeaderboard() {
        isLoading(true)
        leaderboardViewModel.getLeaderboard()
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