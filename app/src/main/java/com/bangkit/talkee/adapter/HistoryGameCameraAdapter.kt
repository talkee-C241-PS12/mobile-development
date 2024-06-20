package com.bangkit.talkee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.talkee.data.response.HistoryDetailItem
import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.databinding.ViewHistoryGameCameraBinding

class HistoryGameCameraAdapter(private val response: HistoryDetailResponse?) : RecyclerView.Adapter<HistoryGameCameraAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewHistoryGameCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryDetailItem, index: Int) {
            binding.question.text = item.jawaban

            val questionNo = "${index + 1}"
            binding.no.isSelected = (item.jawaban == item.jawabanuser)
            binding.no.text = questionNo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewHistoryGameCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        response?.pertanyaan?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return response?.pertanyaan?.size ?: 0
    }
}
