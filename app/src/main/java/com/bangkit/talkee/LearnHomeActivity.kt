package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.LearnHomeAdapter
import com.bangkit.talkee.data.repository.KelasRepository
import com.bangkit.talkee.data.response.KelasItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.KelasViewModel
import com.bangkit.talkee.data.viewmodel.KelasViewModelFactory
import com.bangkit.talkee.databinding.ActivityLearnHomeBinding

class LearnHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnHomeBinding
    private lateinit var learnHomeAdapter: LearnHomeAdapter
    private lateinit var kelasViewModel: KelasViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        learnHomeAdapter = LearnHomeAdapter(null)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = learnHomeAdapter

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = KelasRepository(apiService)
        val kelasViewModelFactory = KelasViewModelFactory(repo)
        kelasViewModel = ViewModelProvider(this, kelasViewModelFactory)[KelasViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        kelasViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                kelasViewModel.clearErrorMessage()
            }
        }

        kelasViewModel.successMessage.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                kelasViewModel.clearSuccessMessage()
            }
        }

        kelasViewModel.kelasResponse.observe(this) { allKelas ->
            isLoading(false)
            if (!(allKelas.data.isNullOrEmpty())) {
                val adapter = LearnHomeAdapter(allKelas)

                adapter.setOnItemClickCallback(object : LearnHomeAdapter.OnItemClickCallback {
                    override fun onItemClicked(kelas: KelasItem) {
                        val i = Intent(this@LearnHomeActivity, LearnListActivity::class.java)
                        i.putExtra(LearnListActivity.SELECTED_ITEM, kelas)
                        startActivity(i)
                    }
                })
                binding.recyclerView.adapter = adapter
            } else {
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        getAllKelas()
    }

    private fun getAllKelas() {
        isLoading(true)
        kelasViewModel.getAllKelas()
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