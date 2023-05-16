package com.example.igwithfirebase.activity_main.frag_user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.igwithfirebase.Variables.PostDtoDiffCallback
import com.example.igwithfirebase.databinding.ItemUserAccountBinding
import com.example.igwithfirebase.model.PostDTO

class UserAccountAdapter
    : ListAdapter<PostDTO, RecyclerView.ViewHolder>(PostDtoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserAccountViewHolder(
            ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curPost = currentList[position] // 현재 글
        with((holder as UserAccountViewHolder).binding) {
            ivImg.load(curPost.imgUrl)
        }
    }

    class UserAccountViewHolder(val binding: ItemUserAccountBinding)
        : RecyclerView.ViewHolder(binding.root)
}