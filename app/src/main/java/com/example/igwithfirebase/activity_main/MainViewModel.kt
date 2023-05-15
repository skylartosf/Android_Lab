package com.example.igwithfirebase.activity_main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.model.FollowDTO
import com.example.igwithfirebase.model.PostDTO

class MainViewModel: ViewModel() {
    // 툴바에 쓰여질 이름
    private val _tbTitle: MutableLiveData<String> = MutableLiveData()
    val tbTitle: LiveData<String> = _tbTitle

    var curUid: String? = null // 현재 위치한 UserAccountFragment의 uid
    var curName: String? = null // curUid의 name(email)

    var theirPosts = arrayListOf<PostDTO>()
    var theirPostUids = arrayListOf<String>()

    // 로그인 해서 MainActivity로 처음 들어왔을 때
    // fs 내 지금 들어온 유저 정보(FollowDto)가 존재하지 않으면 doc을 하나 생성한다
    fun createUserFollowDtoOrNot() {
        Log.d("STARBUCKS", "I'm inside createUserFollowDtoOrNot()")
        UserVars.firestore!!.collection("users")?.document(UserVars.myUid!!)?.get()
            ?.addOnSuccessListener {
                if (!it.exists()) {
                    Log.d("STARBUCKS", "You are not in the db. Let me make a follow DTO for you.")
                    //val followDto = FollowDTO(uid = UserVars.myUid!!)
                    UserVars.firestore!!.collection("users").document(UserVars.myUid!!)
                        .set( FollowDTO(
                            uid = UserVars.myUid!!,
                            name = UserVars.auth!!.currentUser?.email
                        ))
                        .addOnSuccessListener {
                            Log.d("STARBUCKS", "You've made a new doc to 'users' collection in fs!")
                        }
                }
            }
    }

    // curUid 유저의 email을 얻는다
    fun getUserEmail() {
        UserVars.firestore!!.collection("users")?.document(curUid!!)?.get()
            ?.addOnSuccessListener {
                curName = it.data?.get("name") as String?
                _tbTitle.value = curName // 툴바 title 변경
            }
    }
}