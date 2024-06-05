package com.bangkit.talkee.fragment.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.GameOnBoardActivity
import com.bangkit.talkee.HistoryDetailActivity
import com.bangkit.talkee.adapter.HistoryAdapter
import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.data.response.ListHistoryItem
import com.bangkit.talkee.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemsList = mutableListOf<ListHistoryItem>()

        for(i in 1..5) {
            val history = ListHistoryItem(
                "${i-1}",
                "Game $i",
            )

            itemsList.add(history)
        }

        val response = HistoryResponse(itemsList)
        historyAdapter = HistoryAdapter(response)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = historyAdapter

        historyAdapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(id: String) {
                val i = Intent(activity, HistoryDetailActivity::class.java)
                i.putExtra("TYPE", id.toInt())
                startActivity(i)
            }
        })
    }
}