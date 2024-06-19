package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.HistoryDetailItem
import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.databinding.ViewHistoryGameWordBinding
import com.bangkit.talkee.utils.getFixedResources

class HistoryGameWordAdapter(private val response: HistoryDetailResponse?) : RecyclerView.Adapter<HistoryGameWordAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ViewHistoryGameWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryDetailItem, index: Int) {
            val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
            val answers = listOf(item.jawaban1, item.jawaban2, item.jawaban3)
            val resource = getFixedResources(item.jawaban!!)
            binding.questionImage.setImageResource(resource ?: R.drawable.img_bg_game)
            for(i in choices.indices) {
                choices[i].text = answers[i] ?: "A"
                if(choices[i].text == item.jawabanuser) {
                    choices[i].isSelected = true
                    choices[i].setTextColor(ContextCompat.getColor(binding.choice1.context, R.color.white))
                }
            }
            val questionNo = "${index + 1}"
            binding.no.isSelected = (item.jawaban == item.jawabanuser)
            binding.no.text = questionNo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHistoryGameWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        response?.pertanyaan?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return response?.pertanyaan?.size ?: 0
    }
}