package com.example.igwithfirebase.Variables

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import com.example.igwithfirebase.databinding.DialogUploadLoadingBinding
import com.example.igwithfirebase.model.FollowDTO
import com.example.igwithfirebase.model.PostDTO
import com.google.firebase.storage.StorageReference

// Firebase 내 storageRef 위치로 uri를 업로드한다
fun uploadImageToStorage(
    storageRef: StorageReference, imgUri: Uri,
    command: String, postContent: String?, callback: MyCallback
) {
    // 1. firebase - storage 에 이미지 파일(imgUri) 업로드
    storageRef.putFile(imgUri).addOnCompleteListener { task ->
        if (task.isSuccessful) { // storage에 업로드 성공
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                when (command) {
                    Constants.DTO_POST ->
                        linkUrlToFirestore_post(uri.toString(), postContent!!, callback)
                    Constants.DTO_PROFILE_IMG ->
                        linkUrlToFirestore_profile(uri.toString(), callback)
                }
            }
        } else { // storage에 업로드 실패
            Log.d("STARBUCKS", "firebase/firestore에 업로드 실패")
        }
    }
}

private fun linkUrlToFirestore_post(
    storageUrl: String, postContent: String, callback: MyCallback
) {
    val postDto = PostDTO(
        uid = UserVars.myUid,
        userIdEmail = UserVars.auth?.currentUser?.email,
        timestamp = System.currentTimeMillis(),
        imgUrl = storageUrl,
        content = postContent
    )
    UserVars.firestore!!.collection("posts").add(postDto)
        .addOnCompleteListener { fsTask ->
            if (fsTask.isSuccessful) {
                Log.d("STARBUCKS", "Successfully upload the post.")
                callback.uploadPost(true)
            } else {
                Log.d("STARBUCKS", "Failed to upload the post.")
                callback.uploadPost(false)
            }
        }
}

private fun linkUrlToFirestore_profile(
    storageUrl: String, callback: MyCallback
) {
    val profileDto = hashMapOf("imgUrl" to storageUrl)
    UserVars.firestore!!.collection("profileImages").document(UserVars.myUid!!).set(profileDto)
        .addOnCompleteListener { fsTask ->
            if (fsTask.isSuccessful) {
                Log.d("STARBUCKS", "Successfully upload the profile.")
                callback.uploadProfile(true)
            } else {
                Log.d("STARBUCKS", "Failed to upload the profile.")
                callback.uploadProfile(false)
            }
        }
}

fun getRealPathFromUri(context: Context, uri: Uri): String? {
    if (uri.path?.startsWith("/storage") == true) return uri.path!!

    val id = DocumentsContract.getDocumentId(uri).split(":")[1]
    val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
    val selection = MediaStore.Files.FileColumns._ID + " = " + id
    val cursor: Cursor? = context.contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        columns,
        selection,
        null,
        null
    )
    try {
        val columnIndex: Int = cursor?.getColumnIndex(columns[0]) ?: 0
        if (cursor!!.moveToFirst()) {
            return cursor.getString(columnIndex)
        }
    } finally {
        cursor!!.close()
    }
    return null
}

fun Activity.showLoadingDialog(dialog: Dialog, s: String) {
    val binding = DialogUploadLoadingBinding.inflate(layoutInflater)
    dialog.setContentView(binding.root)
    dialog.setCancelable(false)
    binding.tvContent.text = s
    val params: WindowManager.LayoutParams? = dialog.window?.attributes ?: null
    if (params != null) {
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
    }
    dialog.window?.setAttributes(params)
    dialog.show()
}

// [get] uids가 작성한 posts 목록을 구성한다
fun getCurMyPosts(
    uids: List<String>, callback: MyCallback
) {
    var result = mutableListOf<PostDTO>()
    UserVars.firestore!!.collection("posts").get()
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("ABC", "[getCurMyPosts] ${it.result.documents}")
                if (it.result.documents.isNotEmpty()) {
                    for (doc in it.result.documents) {
                        val by = doc.data!!["uid"]
                        if (uids.contains(by))
                            result.add(doc.toObject(PostDTO::class.java)!!)
                    }
                    callback.getMyPosts(true, result)
                }
                else callback.getMyPosts(true, listOf())
            }
        }
            /*
        .addOnSuccessListener { qs ->
            for (doc in qs.documents) { // 각 post를 살펴본다, 해당 uid가 uids 중 하나인지.
                val postUid = doc.data!!.get("uid")
                if (uids.contains(postUid)) result.add(doc.toObject(PostDTO::class.java)!!)
            }
            if (result.isNotEmpty()) callback.getMyPosts(true, result)*/
}

// [get] uid의 프로필 사진을 가져온다
fun getUidProfileImg(
    uid: String, callback: MyCallback
) {
    UserVars.firestore!!.collection("profileImages").document(uid).get()
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("ABC", "[getUidProfileImg] ${it.result.data}")
                if (it.result.data != null) callback.getProfile(true, it.result.data!!["imgUrl"].toString())
                else callback.getProfile(false, "")
            }
        }
}

// 로그인 해서 MainActivity로 처음 들어왔을 때
// fs 내 지금 들어온 유저 정보(FollowDto)가 존재하지 않으면 doc을 하나 생성한다
fun createUserDoc() {
    Log.d("ABC", "I'm inside createUserFollowDtoOrNot()")
    UserVars.firestore!!.collection("users").document(UserVars.myUid!!).get()
        .addOnCompleteListener {
            if (it.isSuccessful && it.result.data != null)
                Log.d("ABC", "You're already in our db.")
            else {
                Log.d("ABC", "Creating new user doc")
                val followDto = FollowDTO(uid = UserVars.myUid!!)
                val email = UserVars.auth!!.currentUser!!.email
                if (email != null) followDto.name = email

                UserVars.firestore!!.collection("users").document(UserVars.myUid!!)
                    .set(followDto)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            Log.d("ABC", "You've made a new doc to 'users' collection in fs!")
                        else
                            Log.d("ABC", "Something went wrong")
                    }
            }
        }
}
