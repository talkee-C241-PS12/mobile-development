package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.LearnHomeResponse
import com.bangkit.talkee.data.response.LearnListResponse
import com.bangkit.talkee.data.response.ListLearnItem
import com.bangkit.talkee.data.response.ListWordItem
import com.bangkit.talkee.databinding.ViewLearnItemBinding
import com.bangkit.talkee.databinding.ViewWordItemBinding

class LearnListAdapter(private val words: LearnListResponse?) : RecyclerView.Adapter<LearnListAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewWordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListWordItem) {
            binding.wordName.text = item.title
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item.title ?: "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewWordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        words?.listWord?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return words?.listWord?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(title: String)
    }

}