package com.example.igwithfirebase.activity_main.act_post

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import com.example.igwithfirebase.Variables.MyCallback
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.Variables.getRealPathFromUri
import com.example.igwithfirebase.Variables.showLoadingDialog
import com.example.igwithfirebase.Variables.uploadImageToStorage
import com.example.igwithfirebase.activity_main.MainActivity
import com.example.igwithfirebase.databinding.ActivityPostBinding
import com.google.common.io.Files
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
        Log.d("ABC", "You've landed on PostActivity")

        registerClickEvents()
    }

    private fun registerClickEvents() {
        // '사진 선택하기' 버튼 클릭 시
        binding.btnSelectPhoto.setOnClickListener {
            // Launch the photo picker and let the user choose only images
            pickOneImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // '사진 올리기' 버튼 클릭 시
        binding.btnUpload.setOnClickListener {
            if (localImgUri == null) {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
            else {
                showLoadingDialog(dialog, Constants.DIALOG_UPLOADING)
                uploadPost()
            }
        }
    }

    private fun uploadPost() {
        val postTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val photoFilePath = getRealPathFromUri(this, localImgUri!!)
        val fileName = "IMAGE_" + postTime + "." + Files.getFileExtension(photoFilePath)
        val storageRef = UserVars.storage!!.reference.child("posts/$fileName")

        Log.d("ABC", "[storageRef] $storageRef")
        //Log.d("PhotoPicker", "[photoFilePath] $photoFilePath")
        //Log.d("PhotoPicker", "[file] $file")

        uploadImageToStorage(storageRef, localImgUri!!, Constants.DTO_POST,
            binding.etContent.text.toString(), object: MyCallback() {
                override fun uploadPost(b: Boolean) {
                    super.uploadPost(b)
                    if (b) {
                        dialog.cancel()
                        Toast.makeText(applicationContext, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()

                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                    }
                }
            }
        )
    }
}