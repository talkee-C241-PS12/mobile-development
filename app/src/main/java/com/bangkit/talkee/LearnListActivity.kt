package com.bangkit.talkee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.LearnHomeAdapter
import com.bangkit.talkee.adapter.LearnListAdapter
import com.bangkit.talkee.data.response.LearnHomeResponse
import com.bangkit.talkee.data.response.LearnListResponse
import com.bangkit.talkee.data.response.ListLearnItem
import com.bangkit.talkee.data.response.ListWordItem
import com.bangkit.talkee.databinding.ActivityGameCameraBinding
import com.bangkit.talkee.databinding.ActivityLearnListBinding

class LearnListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnListBinding
    private lateinit var learnListAdapter: LearnListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsList = mutableListOf<ListWordItem>()
        val title = intent.getStringExtra("TITLE") ?: ""

        binding.title.text = title

        for(i in 1..10) {
            val item = ListWordItem(
                "id$i",
                "$title | Kata $i",
                "Desc kelas $i"
            )

            itemsList.add(item)
        }

        val response = LearnListResponse(itemsList)
        learnListAdapter = LearnListAdapter(response)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = learnListAdapter

        learnListAdapter.setOnItemClickCallback(object : LearnListAdapter.OnItemClickCallback {
            override fun onItemClicked(title: String) {
                val i = Intent(this@LearnListActivity, LearnVideoActivity::class.java)
                i.putExtra("TITLE", title)
                startActivity(i)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }
}