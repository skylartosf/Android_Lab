package com.example.igwithfirebase.nav.model

data class FollowDTO(
    var name: String,
    var followerCnt: Int = 0,
    var followers: MutableMap<String, Boolean> = HashMap(),
    var followingCnt: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap()
)
