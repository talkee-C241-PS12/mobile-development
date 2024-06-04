package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.GameHomeAdapter
import com.bangkit.talkee.adapter.LearnHomeAdapter
import com.bangkit.talkee.data.response.GameHomeResponse
import com.bangkit.talkee.data.response.LearnHomeResponse
import com.bangkit.talkee.data.response.ListGameItem
import com.bangkit.talkee.data.response.ListLearnItem
import com.bangkit.talkee.databinding.ActivityLearnHomeBinding
import com.bangkit.talkee.databinding.ActivityLearnListBinding

class LearnHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnHomeBinding
    private lateinit var learnHomeAdapter: LearnHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsList = mutableListOf<ListLearnItem>()

        for(i in 1..10) {
            val item = ListLearnItem(
                "id$i",
                "url",
                "Kelas $i",
                "Desc kelas $i"
            )

            itemsList.add(item)
        }

        val response = LearnHomeResponse(itemsList)
        learnHomeAdapter = LearnHomeAdapter(response)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = learnHomeAdapter

        learnHomeAdapter.setOnItemClickCallback(object : LearnHomeAdapter.OnItemClickCallback {
            override fun onItemClicked(title: String) {
                val i = Intent(this@LearnHomeActivity, LearnListActivity::class.java)
                i.putExtra("TITLE", title)
                startActivity(i)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }
}