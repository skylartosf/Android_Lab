package com.example.viewbindingrv

import androidx.recyclerview.widget.RecyclerView
import com.example.viewbindingrv.databinding.RvItemBinding

class MainViewHolder(private val binding: RvItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Task) {
        with (binding) {
            tvTitle.text = item.title
            tvTime.text = item.time
        }
    }
}