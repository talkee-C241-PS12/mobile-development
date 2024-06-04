package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.LearnHomeResponse
import com.bangkit.talkee.data.response.ListLearnItem
import com.bangkit.talkee.databinding.ViewLearnItemBinding

class LearnHomeAdapter(private val learnItems: LearnHomeResponse?) : RecyclerView.Adapter<LearnHomeAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewLearnItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListLearnItem) {
//            Glide.with(binding.userPic.context).load(item.avatarUrl).into(binding.userPic)
            binding.learnTitle.text = item.title
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item.title ?: "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewLearnItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        learnItems?.listLearnItem?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return learnItems?.listLearnItem?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(title: String)
    }

}