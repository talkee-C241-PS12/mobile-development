package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.HistoryDetailItem
import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.databinding.ViewHistoryGameSignBinding
import com.bangkit.talkee.utils.getFixedResources

class HistoryGameSignAdapter(private val response: HistoryDetailResponse?) : RecyclerView.Adapter<HistoryGameSignAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewHistoryGameSignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryDetailItem, index: Int) {
            val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
            val choiceImages = listOf(binding.choiceImage1, binding.choiceImage2, binding.choiceImage3)
            val choiceSelectors = listOf(binding.choiceSelector1, binding.choiceSelector2, binding.choiceSelector3)

            val answers = listOf(item.jawaban1, item.jawaban2, item.jawaban3)
            binding.question.text = item.jawaban
            for(i in choices.indices) {
                val resource = getFixedResources(answers[i].toString())
                choiceImages[i].setImageResource(resource ?: R.drawable.img_bg_game)
                if((answers[i] ?: "A") == item.jawabanuser) {
                    choiceSelectors[i].visibility = View.VISIBLE
                }
            }
            val questionNo = "${index + 1}"
            binding.no.isSelected = (item.jawaban == item.jawabanuser)
            binding.no.text = questionNo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHistoryGameSignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        response?.pertanyaan?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return response?.pertanyaan?.size ?: 0
    }
}