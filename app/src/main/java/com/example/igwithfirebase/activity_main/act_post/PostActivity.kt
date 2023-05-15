package com.example.igwithfirebase.activity_main.act_post

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.Constants
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.Variables.fin
import com.example.igwithfirebase.Variables.getRealPathFromUri
import com.example.igwithfirebase.Variables.showLoadingDialog
import com.example.igwithfirebase.Variables.uploadImageToStorage
import com.example.igwithfirebase.databinding.ActivityPostBinding
import com.google.common.io.Files.getFileExtension
import java.text.SimpleDateFormat
import java.util.Date

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private var localImgUri: Uri? = null
    private lateinit var dialog: Dialog

    // Registers a photo picker activity launcher in single-select mode
    private val pickOneImg =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker
            if (uri != null) {
                Log.d("STARBUCKS", "Selected URI: $uri")
                localImgUri = uri
                binding.ivPhoto.load(uri)
            } else Log.d("STARBUCKS", "No media selected")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)

        registerClickEvents()
        registerObservers()
    }

    private fun registerObservers() {
        fin.observe(this) {
            Log.d("STARBUCKS", "THIS is [fin observer] from PostActivity")
            dialog.cancel()
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun registerClickEvents() {
        // '사진 선택하기' 버튼 클릭 시
        binding.btnSelectPhoto.setOnClickListener {
            // Launch the photo picker and let the user choose only images
            pickOneImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // '사진 올리기' 버튼 클릭 시
        binding.btnUpload.setOnClickListener {
            showLoadingDialog(dialog, Constants.DIALOG_UPLOADING)

            val postTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val photoFilePath = localImgUri?.let { uri -> getRealPathFromUri(uri) }
            val fileName = "IMAGE_" + postTime + "." + getFileExtension(photoFilePath)
            val storageRef = UserVars.storage?.reference?.child("posts/$fileName")

            Log.d("STARBUCKS", "[storageRef] $storageRef")
            //Log.d("PhotoPicker", "[photoFilePath] $photoFilePath")
            //Log.d("PhotoPicker", "[file] $file")

            if (storageRef != null) {
                uploadImageToStorage(
                    storageRef, localImgUri,
                    Constants.DTO_POST, binding.etContent.text.toString()
                )
            }
        }
    }

    /*
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
    */
}