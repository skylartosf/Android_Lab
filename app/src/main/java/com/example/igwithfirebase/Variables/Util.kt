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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.igwithfirebase.databinding.DialogUploadLoadingBinding
import com.example.igwithfirebase.model.PostDTO
import com.google.firebase.storage.StorageReference

private val _fin: MutableLiveData<Boolean> = MutableLiveData()
val fin: LiveData<Boolean> = _fin

// Firebase 내 storageRef 위치로 uri를 업로드한다
fun Context.uploadImageToStorage(
    storageRef: StorageReference,
    imgUri: Uri?,
    command: String,
    postContent: String?
) {
    imgUri?.let {
        // 1. firebase - storage 에 이미지 파일(imgUri) 업로드
        storageRef.putFile(it).addOnCompleteListener { task ->
            if (task.isSuccessful) { // storage에 업로드 성공
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    when (command) {
                        Constants.DTO_POST ->
                            linkUrlToFirestore_post(uri.toString(), postContent!!)

                        Constants.DTO_PROFILE_IMG ->
                            linkUrlToFirestore_profile(uri.toString())
                    }
                }
            } else { // storage에 업로드 실패
                Log.d("STARBUCKS", "firebase/firestore에 업로드 실패")
            }
        }
    }
}

private fun linkUrlToFirestore_post(storageUrl: String, postContent: String) {
    val postDto = PostDTO(
        uid = UserVars.myUid,
        userIdEmail = UserVars.auth?.currentUser?.email,
        timestamp = System.currentTimeMillis(),
        imgUrl = storageUrl,
        content = postContent
    )
    UserVars.firestore?.collection("posts")?.add(postDto)
        ?.addOnCompleteListener { fsTask ->
            if (fsTask.isSuccessful) {
                Log.d("STARBUCKS", "Successfully upload the post.")
                _fin.value = true
            } else {
                Log.d("STARBUCKS", "Failed to upload the post.")
            }
        }
}

private fun linkUrlToFirestore_profile(storageUrl: String) {
    var profileDto = hashMapOf("imgUrl" to storageUrl)
    UserVars.firestore?.collection("profileImages")?.document(UserVars.myUid!!)?.set(profileDto)
        ?.addOnCompleteListener { fsTask ->
            if (fsTask.isSuccessful) {
                Log.d("STARBUCKS", "Successfully upload the profile.")
            } else {
                Log.d("STARBUCKS", "Failed to upload the profile.")
            }
        }
}

fun Context.getRealPathFromUri(uri: Uri): String? {
    if (uri.path?.startsWith("/storage") == true) return uri.path!!

    val id = DocumentsContract.getDocumentId(uri).split(":")[1]
    val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
    val selection = MediaStore.Files.FileColumns._ID + " = " + id
    val cursor: Cursor? = contentResolver.query(
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