package com.bangkit.talkee

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.HistoryGameCameraAdapter
import com.bangkit.talkee.adapter.HistoryGameSignAdapter
import com.bangkit.talkee.adapter.HistoryGameWordAdapter
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.HistoryRepository
import com.bangkit.talkee.data.response.HistoryItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.HistoryViewModel
import com.bangkit.talkee.data.viewmodel.HistoryViewModelFactory
import com.bangkit.talkee.databinding.ActivityHistoryDetailBinding
import kotlin.math.roundToInt

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var gameId: String
    private var gameType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading(true)

        val detailHistory = if(Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(SELECTED_ITEM, HistoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(SELECTED_ITEM)
        }

        if (detailHistory != null) {
            gameId = detailHistory.idusergame!!
            binding.historyTitle.text = detailHistory.nama
            binding.historyDate.text = detailHistory.tanggal

            initViewModel()
        } else {
            isLoading(false)
            binding.tvFailedToLoad.visibility = View.VISIBLE
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = HistoryGameWordAdapter(null)

        binding.btnClose.setOnClickListener { finish() }
    }
    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = HistoryRepository(apiService)
        val historyViewModelFactory = HistoryViewModelFactory(repo)
        historyViewModel = ViewModelProvider(this, historyViewModelFactory)[HistoryViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        historyViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                historyViewModel.clearErrorMessage()
            }
        }

        historyViewModel.successMessage.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                historyViewModel.clearSuccessMessage()
            }
        }

        historyViewModel.historyDetailResponse.observe(this) { historyDetail ->
            isLoading(false)
            if (!(historyDetail.pertanyaan.isNullOrEmpty())) {
                val type = historyDetail.pertanyaan[0]?.tipe
                val adapter = when(type) {
                    0 -> HistoryGameCameraAdapter(historyDetail)
                    1 -> HistoryGameWordAdapter(historyDetail)
                    2 -> HistoryGameSignAdapter(historyDetail)
                    else -> HistoryGameWordAdapter(historyDetail)
                }

                val totalQuestions = historyDetail.pertanyaan.size
                val userCorrectAnswers = ((totalQuestions * historyDetail.poinuser!!).toDouble() / historyDetail.poin!!.toDouble()).roundToInt()
                val pointText = "+${historyDetail.poinuser} poin"

                binding.historyTotalQuestions.text = totalQuestions.toString()
                binding.historyUserCorrect.text = userCorrectAnswers.toString()
                binding.historyPoints.text = pointText

                binding.recyclerView.adapter = adapter
                binding.progressCircularHeader.visibility = View.GONE
                binding.historyHeader.visibility = View.VISIBLE
            } else {
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        if(gameId.isNotEmpty()) {
            val tokenManager = TokenManager(this)
            val idToken = tokenManager.getIDToken()

            if(idToken != null) {
                getHistoryDetail(idToken, gameId)
            }
        }
    }

    private fun getHistoryDetail(token: String, idRiwayat: String) {
        isLoading(true)
        historyViewModel.getHistoryDetail(token, idRiwayat)
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
        const val SELECTED_ITEM = "SELECTED_HISTORY"
    }
}