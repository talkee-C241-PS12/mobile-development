package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.HistoryItem
import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.databinding.ViewHistoryItemBinding
import kotlin.math.roundToInt

class HistoryAdapter(private val history: HistoryResponse?) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            val pointText = "+${item.poinuser?.roundToInt()}"
            binding.title.text = item.nama
            binding.historyPoints.text = pointText
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        history?.data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return history?.data?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: HistoryItem)
    }

}