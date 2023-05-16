package com.example.igwithfirebase.Variables

import androidx.recyclerview.widget.DiffUtil
import com.example.igwithfirebase.model.PostDTO

object PostDtoDiffCallback: DiffUtil.ItemCallback<PostDTO>() {
    override fun areItemsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem.imgUrl == newItem.imgUrl
    }

    override fun areContentsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem == newItem
    }
}