package com.example.igwithfirebase.model

data class FollowDTO(
    val uid: String,
    var name: String = "",
    var followerCnt: Int = 0,
    var followers: MutableMap<String, Boolean> = HashMap(),
    var followingCnt: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap()
)
