package com.bangkit.talkee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.LeaderboardAdapter
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.bangkit.talkee.data.response.LearnListResponse
import com.bangkit.talkee.data.response.ListUserItem
import com.bangkit.talkee.data.response.ListWordItem
import com.bangkit.talkee.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsList = mutableListOf<ListUserItem>()

        for(i in 1..10) {
            val item = ListUserItem(
                "id$i",
                "User $i",
                i,
                100 - i
            )

            itemsList.add(item)
        }

        val response = LeaderboardResponse(itemsList)
        leaderboardAdapter = LeaderboardAdapter(response)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = leaderboardAdapter

        leaderboardAdapter.setOnItemClickCallback(object : LeaderboardAdapter.OnItemClickCallback {
            override fun onItemClicked(title: String) {

            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }
}