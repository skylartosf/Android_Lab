package com.example.igwithfirebase.activity_main.frag_user

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.igwithfirebase.databinding.ItemUserAccountBinding
import com.example.igwithfirebase.model.PostDTO

class UserAccountAdapter(
    val myPosts: List<PostDTO>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserAccountViewHolder(
            ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int { return myPosts.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curPost = myPosts[position] // 현재 글
        with((holder as UserAccountViewHolder).binding) {
            //Log.e("STARBUCKS", "[$position] ${curPost.get("imgUrl").toString()}")
            ivImg.load(curPost.imgUrl)
        }
    }

    class UserAccountViewHolder(val binding: ItemUserAccountBinding): RecyclerView.ViewHolder(binding.root)
}