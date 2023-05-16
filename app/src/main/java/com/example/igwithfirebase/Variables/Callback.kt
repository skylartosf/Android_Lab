package com.example.igwithfirebase.Variables

import com.example.igwithfirebase.model.PostDTO

interface MyCallbackInterface {
    // [set] upload a post
    fun uploadPost(b: Boolean)
    // [set] upload a new profile pic
    fun uploadProfile(b: Boolean)
    // [get] get a user profile
    fun getProfile(b: Boolean, url: String)
    // [get] get posts I posted
    fun getMyPosts(list: List<PostDTO>)
}

abstract class MyCallback : MyCallbackInterface {
    override fun uploadPost(b: Boolean) { }
    override fun uploadProfile(b: Boolean) { }
    override fun getProfile(b: Boolean, url: String) { }
    override fun getMyPosts(list: List<PostDTO>) { }
}

