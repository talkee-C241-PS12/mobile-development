package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.LeaderboardItem
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.bangkit.talkee.databinding.ViewLeaderboardItemBinding
import com.bumptech.glide.Glide
import kotlin.math.roundToInt

class LeaderboardAdapter(private val leaderboard: LeaderboardResponse?) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewLeaderboardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderboardItem) {
            binding.userName.text = item.nama
            binding.userRank.text = item.leaderboard.toString()
            binding.userScore.text = item.poin?.roundToInt().toString()
            Glide.with(binding.userProfilePic.context).load(item.image).into(binding.userProfilePic)

            when(item.leaderboard) {
                1 -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_first)
                    binding.userRank.setTextColor(binding.userRank.context.resources.getColor(R.color.rank_first))
                }
                2 -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_second)
                    binding.userRank.setTextColor(binding.userRank.context.resources.getColor(R.color.rank_second))
                }
                else -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_blank)
                    binding.userRank.setTextColor(binding.userRank.context.resources.getColor(R.color.rank_default))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewLeaderboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        leaderboard?.data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return leaderboard?.data?.size ?: 0
    }
}