package com.example.igwithfirebase.nav.homeFeed

data class FeedDTO(
    val uid: String? = null, // 이미지를 올린 유저의 uid
    val userId: String? = null, // 이미지를 올린 유저의 아이디
    var timestamp: Long? = null,
    var favorites: Map<String, Boolean> = HashMap(), // 좋아요를 누른 유저들의 uid
    var favCount: Int = 0,
    var imgUrl: String? = null,
    var contentDescr: String? = null
) {
    data class Comment(
        val uid: String? = null, // 댓글을 단 유저의 uid
        val userId: String? = null, // 댓글을 단 유저의 아이디
        var comment: String? = null,
        var timestamp: Long? = null
    )
}
