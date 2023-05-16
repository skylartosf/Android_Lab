package com.example.igwithfirebase.activity_main.frag_user

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.igwithfirebase.LoginActivity
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.Constants
import com.example.igwithfirebase.Variables.MyCallback
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.Variables.getCurMyPosts
import com.example.igwithfirebase.Variables.getUidProfileImg
import com.example.igwithfirebase.Variables.showLoadingDialog
import com.example.igwithfirebase.Variables.uploadImageToStorage
import com.example.igwithfirebase.activity_main.MainViewModel
import com.example.igwithfirebase.databinding.FragmentUserAccountBinding
import com.example.igwithfirebase.model.PostDTO


class UserAccountFragment : Fragment() {
    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!
    private val mainVm: MainViewModel by activityViewModels()  // ViewModel 초기화
    private lateinit var dialog: Dialog
    private lateinit var myAdapter: UserAccountAdapter

    // 프로필 사진 변경 시 앨범에서 사진 1개 선택해오기
    private val pickOneImg =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) changeProfileImg(uri)
            else Log.e("ABC", "No image for profile selected")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        dialog = context?.let { Dialog(it) }!!

        registerClickEvents()
        setDefaultAccountPageUi()

        myAdapter = UserAccountAdapter()
        binding.rv.adapter = myAdapter

        return binding.root
    }

    private fun registerClickEvents() {
        // 프로필 사진 클릭 시: 프로필 사진 변경 가능
        binding.cvProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickOneImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    // [set] myUid의 프로필 사진을 설정한다(바꾼다)
    private fun changeProfileImg(uri: Uri) {
        activity?.showLoadingDialog(dialog, Constants.DIALOG_UPLOADING_PROFILE)
        binding.ivProfile.load(uri)

        uploadImageToStorage(
            UserVars.storage!!.reference.child("profileImages/${UserVars.myUid}"),
            uri, Constants.DTO_PROFILE_IMG, null, object: MyCallback() {
                override fun uploadProfile(b: Boolean) {
                    super.uploadProfile(b)
                    if (b) {
                        dialog.cancel()
                        Toast.makeText(requireContext(), "프사 변경 성공", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun setDefaultAccountPageUi() {
        activity?.showLoadingDialog(dialog, "Setting up user page!")

        // display 프로필 사진
        getUidProfileImg(mainVm.curUid!!, object: MyCallback() {
            override fun getProfile(b: Boolean, url: String) {
                super.getProfile(b, url)
                if (b) binding.ivProfile.load(url)
                dialog.cancel()
            }
        })

        // display my posts
        getCurMyPosts(listOf(mainVm.curUid!!), object: MyCallback() {
            override fun getMyPosts(list: List<PostDTO>) { // 빈 리스트가 올 수도 있다~
                super.getMyPosts(list)
                myAdapter.submitList(list.toMutableList())
            }
        })

        // 1. 현재 user 페이지 == 내 페이지 -> 로그아웃 버튼, 툴바 기본 설정
        if (mainVm.curUid != "" && mainVm.curUid == UserVars.myUid) {
            with(binding!!) {
                btnLogout.text = getString(R.string.signout)
                btnLogout.setOnClickListener {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                    UserVars.auth?.signOut() //TODO: 로그아웃 제대로 구현
                }
            }
        }
        // 2. 현재 user 페이지 == 다른 유저 페이지 -> 팔로우 버튼, 툴바 없음??
        else {
            with(binding!!) {
                btnLogout.text = getString(R.string.follow)
                //TODO: 툴바가 어떻게 달라야 할까..
                btnLogout.setOnClickListener {
                    //requestFollow()
                }
            }
        }
    }

    private fun requestFollow() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainVm.curUid = ""
    }
}