package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.bangkit.talkee.data.response.LearnListResponse
import com.bangkit.talkee.data.response.ListUserItem
import com.bangkit.talkee.data.response.ListWordItem
import com.bangkit.talkee.databinding.ViewLeaderboardItemBinding

class LeaderboardAdapter(private val users: LeaderboardResponse?) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewLeaderboardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListUserItem) {
            binding.userName.text = item.name
            binding.userRank.text = item.rank.toString()
            binding.userScore.text = item.score.toString()

            when (item.rank) {
                1 -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_first)
                }
                2 -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_second)
                }
                else -> {
                    binding.iconRank.setImageResource(R.drawable.ic_medal_blank)
                }
            }
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item.name ?: "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewLeaderboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        users?.listUser?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return users?.listUser?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(title: String)
    }

}