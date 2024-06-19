package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.KelasItem
import com.bangkit.talkee.data.response.KelasResponse
import com.bangkit.talkee.databinding.ViewLearnItemBinding
import com.bumptech.glide.Glide

class LearnHomeAdapter(private val learnItems: KelasResponse?) : RecyclerView.Adapter<LearnHomeAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(private val binding: ViewLearnItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KelasItem) {
            Glide.with(binding.learnImage.context).load(item.image).into(binding.learnImage)
            binding.learnTitle.text = item.namakelas
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewLearnItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        learnItems?.data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return learnItems?.data?.size ?: 0
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(kelas: KelasItem)
    }

}