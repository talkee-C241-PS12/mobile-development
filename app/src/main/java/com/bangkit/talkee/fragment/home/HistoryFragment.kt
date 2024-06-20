package com.bangkit.talkee.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.HistoryDetailActivity
import com.bangkit.talkee.R
import com.bangkit.talkee.adapter.HistoryAdapter
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.HistoryRepository
import com.bangkit.talkee.data.response.HistoryItem
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.HistoryViewModel
import com.bangkit.talkee.data.viewmodel.HistoryViewModelFactory
import com.bangkit.talkee.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        historyAdapter = HistoryAdapter(null)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = historyAdapter

        historyAdapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(item: HistoryItem) {
                val i = Intent(activity, HistoryDetailActivity::class.java)
                i.putExtra(HistoryDetailActivity.SELECTED_ITEM, item)
                startActivity(i)
            }
        })
    }

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = HistoryRepository(apiService)
        val kelasViewModelFactory = HistoryViewModelFactory(repo)
        historyViewModel = ViewModelProvider(this, kelasViewModelFactory)[HistoryViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        historyViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                historyViewModel.clearErrorMessage()
            }
        }

        historyViewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            if (successMessage.isNotEmpty()) {
                historyViewModel.clearSuccessMessage()
            }
        }

        historyViewModel.historyResponse.observe(viewLifecycleOwner) { allKelas ->
            isLoading(false)
            if (!(allKelas.data.isNullOrEmpty())) {
                val adapter = HistoryAdapter(allKelas)

                adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
                    override fun onItemClicked(item: HistoryItem) {
                        val i = Intent(activity, HistoryDetailActivity::class.java)
                        i.putExtra(HistoryDetailActivity.SELECTED_ITEM, item)
                        startActivity(i)
                    }
                })
                binding.recyclerView.adapter = adapter
            } else {
                binding.tvFailedToLoad.text = getString(R.string.empty_history)
                binding.tvFailedToLoad.visibility = View.VISIBLE
            }
        }

        getAllHistory()
    }

    private fun getAllHistory() {
        isLoading(true)
        val tokenManager = TokenManager(requireContext())
        val idToken = tokenManager.getIDToken()

        if (idToken != null) {
            historyViewModel.getAllHistory(idToken)
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
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}