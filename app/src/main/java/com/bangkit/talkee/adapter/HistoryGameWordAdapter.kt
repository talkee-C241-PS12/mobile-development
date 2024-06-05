package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.HistoryGameResponse
import com.bangkit.talkee.data.response.ListQuestionItem
import com.bangkit.talkee.databinding.ViewHistoryGameWordBinding

class HistoryGameWordAdapter(private val questions: HistoryGameResponse?) : RecyclerView.Adapter<HistoryGameWordAdapter.ViewHolder>() {

    private val fixedResources: Map<String, Int> = mapOf(
        "A" to R.drawable.hand_a,
        "D" to R.drawable.hand_d,
        "O" to R.drawable.hand_o
    )

    inner class ViewHolder(private val binding: ViewHistoryGameWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListQuestionItem) {
            val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
            val answers = item.answers?.split(",")
            val resource = fixedResources[item.question]
            binding.questionImage.setImageResource(resource ?: R.drawable.img_bg_game)
            for(i in choices.indices) {
                choices[i].text = answers?.get(i) ?: "A"
                if(choices[i].text == item.answer) {
                    choices[i].isSelected = true
                    choices[i].setTextColor(ContextCompat.getColor(binding.choice1.context, R.color.white))
                }
            }
            binding.no.isSelected = item.isCorrect == "true"
            binding.no.text = item.questionNo.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHistoryGameWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        questions?.listQuestion?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return questions?.listQuestion?.size ?: 0
    }
}