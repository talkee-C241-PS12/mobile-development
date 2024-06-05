package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.R
import com.bangkit.talkee.data.response.HistoryGameResponse
import com.bangkit.talkee.data.response.ListQuestionItem
import com.bangkit.talkee.databinding.ViewHistoryGameSignBinding
import com.bangkit.talkee.databinding.ViewHistoryGameWordBinding

class HistoryGameSignAdapter(private val questions: HistoryGameResponse?) : RecyclerView.Adapter<HistoryGameSignAdapter.ViewHolder>() {

    private val fixedResources: Map<String, Int> = mapOf(
        "A" to R.drawable.hand_a,
        "D" to R.drawable.hand_d,
        "O" to R.drawable.hand_o
    )

    inner class ViewHolder(private val binding: ViewHistoryGameSignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListQuestionItem) {
            val choices = listOf(binding.choice1, binding.choice2, binding.choice3)
            val choiceImages = listOf(binding.choiceImage1, binding.choiceImage2, binding.choiceImage3)
            val choiceSelectors = listOf(binding.choiceSelector1, binding.choiceSelector2, binding.choiceSelector3)

            val answers = item.answers?.split(",")
            binding.question.text = item.question
            for(i in choices.indices) {
                val resource = fixedResources[answers?.get(i)]
                choiceImages[i].setImageResource(resource ?: R.drawable.img_bg_game)
                if((answers?.get(i) ?: "A") == item.answer) {
                    choiceSelectors[i].visibility = View.VISIBLE
                }
            }
            binding.no.isSelected = item.isCorrect == "true"
            binding.no.text = item.questionNo.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHistoryGameSignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        questions?.listQuestion?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return questions?.listQuestion?.size ?: 0
    }
}