package com.example.igwithfirebase.nav

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.igwithfirebase.R
import com.example.igwithfirebase.databinding.ActivityAddPhotoBinding
import com.example.igwithfirebase.databinding.DialogUploadBinding
import com.example.igwithfirebase.nav.homeFeed.postDTO
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class AddPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding

    private lateinit var fbStorage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var photoUri: Uri? = null
    //private var photoFilePath: String? = null
    // Registers a photo picker activity launcher in single-select mode
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            //photoFilePath = getRealPathFromUri(uri)
            photoUri = uri
            binding.ivPhoto.load(uri)
        }
        else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fbStorage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        toSelectPhoto()
        toUploadPhoto()
    }

    private fun toSelectPhoto() {
        binding.btnSelectPhoto.setOnClickListener {
            // Launch the photo picker and let the user choose only images
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun toUploadPhoto() {
        binding.btnUpload.setOnClickListener {
            if (photoUri != null) {
                val loadingDialog = Dialog(this)
                loadingDialog.setContentView(DialogUploadBinding.inflate(layoutInflater).root)
                loadingDialog.setCancelable(false)
                loadingDialog.show()
                loadingDialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

                val postTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val photoFilePath = getRealPathFromUri(photoUri!!)
                val imgFileName = "IMAGE_" + postTime + "." + getFileExtension(photoFilePath)

                // 업로드할 위치 + /파일명
                val storageRef = fbStorage.reference.child("images/$imgFileName")

                Log.d("PhotoPicker", "[storageRef] $storageRef")
                //Log.d("PhotoPicker", "[photoFilePath] $photoFilePath")
                //Log.d("PhotoPicker", "[file] $file")

                // 1. firebase storage에 이미지 업로드
                storageRef.putFile(photoUri!!)
                    .addOnSuccessListener { taskSnapshot ->

                        // 2. firesstore에 해당 이미지를 참조하는 document(postDTO) 생성
                        val postDTO = postDTO(
                            uid = auth.currentUser?.uid,
                            userIdEmail = auth.currentUser?.email,
                            timestamp = System.currentTimeMillis(),
                            imgUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString(),
                            content = binding.etContent.text.toString()
                        )

                        // 게시물 데이터 생성
                        firestore.collection("images").document().set(postDTO)
                            .addOnSuccessListener {
                                loadingDialog.cancel()
                                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                    .addOnFailureListener {
                        loadingDialog.cancel()
                        finish()
                        Toast.makeText(this, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
                        Log.d("PhotoPicker", "Fail to upload on firestore: ${it.message}")
                    }
            }
            else {
                Toast.makeText(this, "Select image please.", Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No PhotoUri selected but you've tried to upload this post.")
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        if (uri.path?.startsWith("/storage") == true) {
            return uri.path!!
        }
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
}