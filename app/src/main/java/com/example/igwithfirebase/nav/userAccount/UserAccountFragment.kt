package com.example.igwithfirebase.nav.userAccount

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.igwithfirebase.LoginActivity
import com.example.igwithfirebase.MainViewModel
import com.example.igwithfirebase.R
import com.example.igwithfirebase.databinding.FragmentUserAccountBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage


class UserAccountFragment : Fragment() {
    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!
    private val mainVm: MainViewModel by activityViewModels()  // ViewModel 초기화
    lateinit var myAdapter: UserAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        // 기초적인 첫 UserAccountFragment의 UI 설정
        mainVm.myProfileUrl.observe(viewLifecycleOwner) {
            if (mainVm.myProfileUrl != null)
                binding.ivProfile.load(mainVm.myProfileUrl.value)
        }

        mainVm.getProfile()
        setAccountPage()

        // 프로필 사진 클릭 시, 프로필 사진 변경 가능
        binding.ivProfile.setOnClickListener { onClickProfile() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainVm.queryResult.observe(viewLifecycleOwner) {
            myAdapter = UserAccountAdapter(it)
            binding.rv.adapter = myAdapter
        }

        mainVm.getPostsByCurUid()
    }

    /****************************** 프로필 사진 변경 ******************************/
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                //TODO: 대기하라는 다이얼로그 띄우기
                Log.d("STARBUCKS", "Selected URI: $uri")
                mainVm.changeProfile(uri)
                binding.ivProfile.load(uri)
                //TODO: 대기하라는 다이얼로그 닫아주기
            } else Log.e("STARBUCKS", "No image for profile selected")
        }

    private fun onClickProfile() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    /***************************************************************************/

    private fun setAccountPage() {
        Log.d("STARBUCKS", "curName is ${mainVm.curName}")

        // 1. 현재 user 페이지 == 내 페이지 -> 로그아웃 버튼, 툴바 기본 설정
        if (mainVm.curUid != null && mainVm.curUid == mainVm.myUid) {
            with(binding!!) {
                btnLogout.text = getString(R.string.signout)
                btnLogout.setOnClickListener {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                    mainVm.auth?.signOut() //TODO: 로그아웃 제대로 구현
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