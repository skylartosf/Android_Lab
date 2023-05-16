package com.example.igwithfirebase.activity_main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.model.FollowDTO
import com.example.igwithfirebase.model.PostDTO

class MainViewModel: ViewModel() {
    // 툴바에 쓰여질 이름 - mainActivity에서 observe 중
    private val _tbTitle: MutableLiveData<String> = MutableLiveData()
    val tbTitle: LiveData<String> = _tbTitle

    var curUid: String = "" // 현재 위치한 UserAccountFragment의 uid
    var curName: String = "" // curUid의 name(email)

    var theirPosts = arrayListOf<PostDTO>()
    var theirPostUids = arrayListOf<String>()

    // curUid 유저의 email을 얻는다
    fun getUserEmail(myEmail: String?, uid: String) {
        //if (myEmail != null) _tbTitle.value = myEmail!! // 바텀네비로 UserAccountFrag 이동할 때
        //else { // 타 유저의 UserAccountFrag 이동할 때
            UserVars.firestore!!.collection("users").document(uid).get()
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.data!!["name"] != "") {
                            curName = it.result.data!!["name"] as String
                            _tbTitle.value = curName
                        }
                        else _tbTitle.value = "love skylar!"
                    }
                }
        //}
    }

    fun setToolbarTitle(s: String) {
        _tbTitle.value = s
    }
}