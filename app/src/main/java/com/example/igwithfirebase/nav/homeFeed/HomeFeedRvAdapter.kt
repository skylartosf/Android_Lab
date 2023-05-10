package com.example.igwithfirebase.nav.homeFeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.igwithfirebase.MainActivity
import com.example.igwithfirebase.R
import com.example.igwithfirebase.databinding.ItemHomeFeedBinding
import com.example.igwithfirebase.nav.UserAccountFragment
import com.example.igwithfirebase.nav.model.FollowDTO
import com.example.igwithfirebase.nav.model.PostDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFeedRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val postDTOs: ArrayList<PostDTO> = ArrayList()
    private val postUidList: ArrayList<String> = ArrayList()

    private var firestore = FirebaseFirestore.getInstance()
    private var imgSnapshot: ListenerRegistration? = null

    init {
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var userDTO = task.result.toObject(FollowDTO::class.java)
                if (userDTO?.followings != null) {
                    getContents(userDTO?.followings)
                }
            } else {
                Log.d("STARBUCKS", "Getting all the user names, failed")
            }
        }
    }

    private fun getContents(followers: MutableMap<String, Boolean>?) {
        imgSnapshot = firestore?.collection("images")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, fbFsException ->
                postDTOs.clear()
                postUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(PostDTO::class.java)!!
                    if (followers?.keys?.contains(item.uid)!!) {
                        postDTOs.add(item)
                        postUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemHomeFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return postDTOs.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // profile image 가져오기
        firestore?.collection("profileImages")?.document(postDTOs[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    (holder as ViewHolder).bind(postDTOs[position], task.result["image"], position)
                }
            }
    }

    class ViewHolder(private val binding: ItemHomeFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDTO, profileUrl: Any?, position: Int) {
            binding.ivProfile.load(profileUrl) // profile 사진
            binding.ivProfile.setOnClickListener {// 클릭 시 UserAccountFragment로 이동
                val fragment = UserAccountFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", item.uid)
                bundle.putString("userId", item.userIdEmail)
                fragment.arguments = bundle

                MainActivity().replaceFragment(fragment)
            }

            binding.tvUsername.text = item.uid // userId
            binding.ivContent.load(item.imgUrl) // 본문 사진
            binding.tvContent.text = item.content // 본문 설정

            binding.iconFav.setOnClickListener {
                HomeFeedRvAdapter().clickFav(position)
            }

            // 좋아요 아이콘 설정
            if (item.favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
                binding.iconFav.setImageResource(R.drawable.ic_favorite)
            }
            else {
                binding.iconFav.setImageResource(R.drawable.ic_favorite_border)
            }

            // 좋아요 개수 설정
            binding.tvFavCount.text = "좋아요 " + item.favCount + "개"

            // 댓글 아이콘 설정
            binding.iconComment.setOnClickListener {
                // TODO: CommentActivity로 intent를 통해 넘어간다
            }
        }


    }

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
}
