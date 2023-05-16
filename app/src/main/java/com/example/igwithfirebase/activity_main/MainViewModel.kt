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

    var curUid: String = "" // 현재 위치한 UserAccountFragment의 uid
    var curName: String = "" // curUid의 name(email)

    var theirPosts = arrayListOf<PostDTO>()
    var theirPostUids = arrayListOf<String>()

    // curUid 유저의 email을 얻는다
    fun getUserEmail(uid: String) {
        UserVars.firestore!!.collection("users").document(uid).get()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.data!!["name"] != null) {
                        curName = it.result.data!!["name"] as String
                        _tbTitle.value = curName
                    }
                    else _tbTitle.value = "love skylar!"
                }
                else {

                }
            }
                /*
            ?.addOnSuccessListener {
                Log.e("ABC", "it is <$it>, it.data is <$it.data>, it.metadata is <${it.metadata}>, it.id is <${it.id}>, it[name] is <{${it["name"]}}>")
                if (it["name"] != null) {
                    curName = it["name"] as String
                    _tbTitle.value = curName // 툴바 title 변경
                }
                else _tbTitle.value = "good"
            }*/
    }
}