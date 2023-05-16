package com.example.igwithfirebase.activity_main.frag_gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.igwithfirebase.databinding.ItemUserAccountBinding
import com.example.igwithfirebase.model.PostDTO

class SearchGalleryAdapter
    : ListAdapter<PostDTO, RecyclerView.ViewHolder>(SearchGalleryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchGalleryViewHolder(
            ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curPost = currentList[position] // 현재 글
        with((holder as SearchGalleryViewHolder).binding) {
            ivImg.load(curPost.imgUrl)
        }
    }

    class SearchGalleryViewHolder(val binding: ItemUserAccountBinding) :
        RecyclerView.ViewHolder(binding.root)
}

object SearchGalleryDiffCallback : DiffUtil.ItemCallback<PostDTO>() {
    override fun areItemsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem.imgUrl == newItem.imgUrl
    }

    override fun areContentsTheSame(oldItem: PostDTO, newItem: PostDTO): Boolean {
        return oldItem == newItem
    }

}