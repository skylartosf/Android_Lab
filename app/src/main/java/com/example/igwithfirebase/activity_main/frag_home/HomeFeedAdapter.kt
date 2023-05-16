package com.example.igwithfirebase.activity_main.frag_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.PostDtoDiffCallback
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.activity_main.MainActivity
import com.example.igwithfirebase.activity_main.frag_user.UserAccountFragment
import com.example.igwithfirebase.databinding.ItemHomeFeedBinding
import com.example.igwithfirebase.model.PostDTO

// notifyDataSetChanged() 언제 써야 돼?
class HomeFeedAdapter
    : ListAdapter<PostDTO, RecyclerView.ViewHolder> (PostDtoDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HomeFeedViewHolder(
            ItemHomeFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curPost = currentList[position] // 현재 글
        with((holder as HomeFeedViewHolder).binding) {
            // profile 사진
            ivProfile.load(curPost.imgUrl) // profile 사진
            ivProfile.setOnClickListener {// 클릭 시 UserAccountFragment로 이동
                val fragment = UserAccountFragment()
                val bundle = Bundle()
                bundle.putString("uid", curPost.uid)
                bundle.putString("name", curPost.userIdEmail)
                fragment.arguments = bundle
                MainActivity().replaceFragment(fragment)
            }

            tvUsername.text = curPost.userIdEmail // userId
            ivContent.load(curPost.imgUrl) // 본문 사진
            tvContent.text = curPost.content // 본문 설정

            // 좋아요 아이콘
            if (curPost.favorites[UserVars.myUid] == true)
                iconFav.setImageResource(R.drawable.ic_favorite)
            else
                iconFav.setImageResource(R.drawable.ic_favorite_border)
            iconFav.setOnClickListener {
                //HomeFeedRvAdapter().clickFav(position)
            }

            // 좋아요 개수
            tvFavCount.text = "좋아요 " + curPost.favCount + "개"

            // 댓글 아이콘
            iconComment.setOnClickListener {
                // TODO: CommentActivity로 intent를 통해 넘어간다
            }

        }
    }

    class HomeFeedViewHolder(val binding: ItemHomeFeedBinding)
        : RecyclerView.ViewHolder(binding.root)

    /*
    // 좋아요 버튼 클릭 시
    fun clickFav(position: Int) {
        val tsDoc = firestore?.collection("images")?.document(postUidList[position])
        firestore?.runTransaction { transaction ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val postDTO = transaction.get(tsDoc!!).toObject(PostDTO::class.java)

            if (postDTO!!.favorites.containsKey(uid)) {
                // Unstar the post and remove self from favs
                postDTO.favCount--
                postDTO.favorites.remove(uid)
            }
            else {
                // Like the post and add self to stars
                postDTO.favCount++
                postDTO.favorites[uid.toString()] = true
                ringFavAlarm(postDTOs[position].uid!!)
            }
            transaction.set(tsDoc, postDTO)
        }
    }

    // 좋아요 버튼을 눌러서, 상대에게 푸쉬알림을 울린다
    fun ringFavAlarm(destinationUid: String) {
        // TODO: 좋아요 알림을 울린다
    }
    */
}
