package com.example.igwithfirebase.Variables

import com.example.igwithfirebase.model.PostDTO

// TODO: 함수들 이름 after~ 로 바꿔야겠다, ~한 다음에 실행할 것들을 정의하기 위한 함수들이니.
interface MyCallbackInterface {
    // [set] upload a post
    fun uploadPost(b: Boolean)
    // [set] upload a new profile pic
    fun uploadProfile(b: Boolean)
    // [get] get a user profile
    fun getProfile(b: Boolean, url: String)
    // [get] get posts I posted
    fun getMyPosts(list: List<PostDTO>)
    // (누군가의) followings 목록을 구한 후 할 일
    fun afterGettingFollowings(list: List<String>)
}

abstract class MyCallback : MyCallbackInterface {
    override fun uploadPost(b: Boolean) { }
    override fun uploadProfile(b: Boolean) { }
    override fun getProfile(b: Boolean, url: String) { }
    override fun getMyPosts(list: List<PostDTO>) { }
    override fun afterGettingFollowings(list: List<String>) { }
}

