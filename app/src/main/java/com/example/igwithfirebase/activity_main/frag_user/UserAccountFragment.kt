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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.igwithfirebase.LoginActivity
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.Constants
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.Variables.fin
import com.example.igwithfirebase.Variables.showLoadingDialog
import com.example.igwithfirebase.Variables.uploadImageToStorage
import com.example.igwithfirebase.activity_main.MainViewModel
import com.example.igwithfirebase.databinding.FragmentUserAccountBinding


class UserAccountFragment : Fragment() {
    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!
    private val mainVm: MainViewModel by activityViewModels()  // ViewModel 초기화
    private lateinit var dialog: Dialog
    lateinit var myAdapter: UserAccountAdapter

    // 프로필 사진 변경 시 앨범에서 사진 1개 선택해오기
    private val pickOneImg =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) changeProfileImg(uri)
            else Log.e("STARBUCKS", "No image for profile selected")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        dialog = context?.let { Dialog(it) }!! // 여기 말고 다른 데 배치하면 initialize 안 됐대,,왜지

        registerClickEvents()
        registerObservers()

        setDefaultAccountPage()
        mainVm.getProfile()
        mainVm.getPostsByCurUid()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun registerObservers() {
        // profile 사진 (있으면) display
        mainVm.myProfileUrl.observe(viewLifecycleOwner) {
            if (mainVm.myProfileUrl != null) binding.ivProfile.load(mainVm.myProfileUrl.value)
        }

        // adapter 설정
        mainVm.queryResult.observe(viewLifecycleOwner) {
            myAdapter = UserAccountAdapter(it)
            binding.rv.adapter = myAdapter
        }

        // profile 사진 변경 시: 반영됨
        fin.observe(viewLifecycleOwner) {
            Log.d("STARBUCKS", "THIS is [fin observer] from UserAccountFragment")
            dialog.cancel()
        }
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

    private fun changeProfileImg(uri: Uri?) {
        activity?.showLoadingDialog(dialog, Constants.DIALOG_UPLOADING_PROFILE)

        Log.d("STARBUCKS", "[changeProfileImg] $uri")
        //mainVm.changeProfile(uri)

        context?.uploadImageToStorage(
            UserVars.storage!!.reference.child("profileImages/${UserVars.myUid}"),
            uri, Constants.DTO_PROFILE_IMG, null
        )
        binding.ivProfile.load(uri)
    }

    private fun setDefaultAccountPage() {
        Log.d("STARBUCKS", "curName is ${mainVm.curName}")

        // 1. 현재 user 페이지 == 내 페이지 -> 로그아웃 버튼, 툴바 기본 설정
        if (mainVm.curUid != null && mainVm.curUid == UserVars.myUid) {
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
        mainVm.curUid = null
    }
}