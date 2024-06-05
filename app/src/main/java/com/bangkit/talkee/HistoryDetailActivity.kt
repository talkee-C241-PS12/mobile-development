package com.bangkit.talkee

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.talkee.adapter.HistoryAdapter
import com.bangkit.talkee.adapter.HistoryGameSignAdapter
import com.bangkit.talkee.adapter.HistoryGameWordAdapter
import com.bangkit.talkee.data.response.HistoryGameResponse
import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.data.response.ListHistoryItem
import com.bangkit.talkee.data.response.ListQuestionItem
import com.bangkit.talkee.databinding.ActivityHistoryDetailBinding

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private val questions = listOf(
        listOf("A", "D", "O"),
        listOf("D", "O", "A"),
        listOf("O", "D", "A"),
        listOf("O", "A", "D"),
        listOf("A", "O", "D"),
        listOf("D", "A", "O"),
    )
    private val answers = listOf("A", "D", "A", "O", "D", "A")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getIntExtra("TYPE", 0)

        val itemsList = mutableListOf<ListQuestionItem>()

        for(i in answers.indices) {
            val history = ListQuestionItem(
                "id$i",
                0,
                i + 1,
                answers[i],
                questions[i].joinToString(","),
                questions[i][i % 2],
                (i % 2 == 0).toString(),
            )

            itemsList.add(history)
        }

        val response = HistoryGameResponse(itemsList)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = when(type) {
            0 -> HistoryGameWordAdapter(response)
            1 -> HistoryGameSignAdapter(response)
            else -> HistoryGameWordAdapter(response)
        }

        binding.btnClose.setOnClickListener { finish() }
    }
}