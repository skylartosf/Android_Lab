package com.example.recycler_view

import androidx.recyclerview.widget.RecyclerView
import com.example.recycler_view.databinding.ItemRvBinding

class ViewHolder(val binding: ItemRvBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(item: DataModel) {
        with(binding) {
            tvTitle.text = item.title
            tvTime.text = item.time
        }
    }
}