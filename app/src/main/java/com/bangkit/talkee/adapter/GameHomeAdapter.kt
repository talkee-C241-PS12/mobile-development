package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.GameItem
import com.bangkit.talkee.data.response.GameResponse
import com.bangkit.talkee.databinding.ViewGameItemBinding
import com.bumptech.glide.Glide

class GameHomeAdapter(private val games: GameResponse?) : RecyclerView.Adapter<GameHomeAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewGameItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GameItem) {
            val pointsText = "+${item.poin} poin"
            Glide.with(binding.gameImage.context).load(item.image).into(binding.gameImage)
            binding.gameTitle.text = item.nama
            binding.gamePoints.text = pointsText
//            binding.gameDesc.text = item.image
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        games?.data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return games?.data?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(game: GameItem)
    }

}