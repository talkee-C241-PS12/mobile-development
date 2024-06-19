package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.KelasDetailItem
import com.bangkit.talkee.data.response.KelasDetailResponse
import com.bangkit.talkee.databinding.ViewWordItemBinding

class LearnListAdapter(private val detailKelas: KelasDetailResponse?) : RecyclerView.Adapter<LearnListAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewWordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KelasDetailItem) {
            binding.title.text = item.kata
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewWordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        detailKelas?.data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return detailKelas?.data?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(detailKelas: KelasDetailItem)
    }
}