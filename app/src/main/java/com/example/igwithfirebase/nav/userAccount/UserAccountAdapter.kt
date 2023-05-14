package com.example.igwithfirebase.nav.userAccount

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.igwithfirebase.databinding.ItemUserAccountBinding
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.firestore.DocumentSnapshot

class UserAccountAdapter(
    val myPosts: List<DocumentSnapshot>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e("STARBUCKS", "[adapter] size of myPosts = ${myPosts.size}")
        return UserAccountViewHolder(
            ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int { return myPosts.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curPost = myPosts[position] // 현재 글
        with((holder as UserAccountViewHolder).binding) {
            Log.e("STARBUCKS", "[$position] ${curPost.get("imgUrl").toString()}")
            ivImg.load(curPost.get("imgUrl").toString())
        }
    }

    class UserAccountViewHolder(val binding: ItemUserAccountBinding): RecyclerView.ViewHolder(binding.root)
}