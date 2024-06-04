package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.GameHomeResponse
import com.bangkit.talkee.data.response.ListGameItem
import com.bangkit.talkee.databinding.ViewGameItemBinding

class GameHomeAdapter(private val games: GameHomeResponse?) : RecyclerView.Adapter<GameHomeAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewGameItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListGameItem) {
//            Glide.with(binding.userPic.context).load(item.avatarUrl).into(binding.userPic)
            binding.gameTitle.text = item.title
            binding.gameDesc.text = item.description
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item.title ?: "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        games?.listGame?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return games?.listGame?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(title: String)
    }

}