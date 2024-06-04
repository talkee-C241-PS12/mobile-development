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
import com.bangkit.talkee.data.response.GameHomeResponse
import com.bangkit.talkee.data.response.ListGameItem
import com.bangkit.talkee.databinding.ActivityGameHomeBinding

class GameHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameHomeBinding
    private lateinit var gameHomeAdapter: GameHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsList = mutableListOf<ListGameItem>()

        for(i in 1..10) {
            val game = ListGameItem(
                "id$i",
                "url",
                "Game $i",
                "Desc game $i"
            )

            itemsList.add(game)
        }

        val response = GameHomeResponse(itemsList)
        gameHomeAdapter = GameHomeAdapter(response)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = gameHomeAdapter

        gameHomeAdapter.setOnItemClickCallback(object : GameHomeAdapter.OnItemClickCallback {
            override fun onItemClicked(title: String) {
                val i = Intent(this@GameHomeActivity, GameOnBoardActivity::class.java)
                i.putExtra("TITLE", title)
                i.putExtra("STATE", "STATE_START")
                startActivity(i)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }
}