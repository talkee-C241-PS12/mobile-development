package com.bangkit.talkee

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.LearnListAdapter
import com.bangkit.talkee.data.repository.KelasRepository
import com.bangkit.talkee.data.response.KelasDetailItem
import com.bangkit.talkee.data.response.KelasItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.KelasViewModel
import com.bangkit.talkee.data.viewmodel.KelasViewModelFactory
import com.bangkit.talkee.databinding.ActivityLearnListBinding

class LearnListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnListBinding
    private lateinit var learnListAdapter: LearnListAdapter
    private lateinit var kelasViewModel: KelasViewModel
    private lateinit var kelasItem: KelasItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailKelas = if(Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(SELECTED_ITEM, KelasItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(SELECTED_ITEM)
        }

        if (detailKelas != null) {
            kelasItem = detailKelas
            binding.title.text = kelasItem.namakelas
        }

        initViewModel()

        learnListAdapter = LearnListAdapter(null)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = learnListAdapter

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

        kelasViewModel.kelasDetailResponse.observe(this) { allDetailKelas ->
            isLoading(false)
            if (!(allDetailKelas.data.isNullOrEmpty())) {
                val adapter = LearnListAdapter(allDetailKelas)

                adapter.setOnItemClickCallback(object : LearnListAdapter.OnItemClickCallback {
                    override fun onItemClicked(detailKelas: KelasDetailItem) {
                        val i = Intent(this@LearnListActivity, LearnVideoActivity::class.java)
                        i.putExtra(LearnVideoActivity.SELECTED_ITEM, detailKelas)
                        startActivity(i)
                    }
                })
                binding.recyclerView.adapter = adapter
            } else {
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        kelasItem.idkelas?.let { getKelasDetail(it) }
    }

    private fun getKelasDetail(idKelas: String) {
        isLoading(true)
        kelasViewModel.getAllKelasDetail(idKelas)
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
        const val SELECTED_ITEM = "SELECTED_KELAS"
    }
}