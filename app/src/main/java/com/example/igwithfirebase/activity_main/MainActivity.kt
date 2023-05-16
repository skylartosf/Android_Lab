package com.example.igwithfirebase.activity_main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.Constants
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.activity_main.act_post.PostActivity
import com.example.igwithfirebase.activity_main.frag_alarm.FavAlarmFragment
import com.example.igwithfirebase.activity_main.frag_gallery.SearchGalleryFragment
import com.example.igwithfirebase.activity_main.frag_home.HomeFeedFragment
import com.example.igwithfirebase.activity_main.frag_user.UserAccountFragment
import com.example.igwithfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val mainVm by viewModels<MainViewModel>()  // ViewModel 초기화

    private val startPostActivityOnResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
            if (binding.bottomNav.selectedItemId != R.id.nav_account)
                binding.bottomNav.selectedItemId = R.id.nav_account
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //mainVm.createUserFollowDtoOrNot()

        setToolbar()
        setNavigation()
        binding.bottomNav.selectedItemId = R.id.nav_account
    }

    private fun setToolbar() {
        mainVm.tbTitle.observe(this) {
            binding.myToolbar.title = mainVm.tbTitle.value
        }
        setSupportActionBar(binding.myToolbar) // toolbar를 activity의 app bar로 지정
        if (supportActionBar != null) { // activate back button on toolbar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
    private fun setNavigation() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFeedFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_search -> {
                    replaceFragment(SearchGalleryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_add_photo -> {
                    //startActivity(Intent(this, PostActivity::class.java))
                    startPostActivityOnResult.launch(Intent(this, PostActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.nav_fav_alarm -> {
                    replaceFragment(FavAlarmFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_account -> {
                    Log.d("ABC", "Selected account page in bottom nav")
                    Log.d("ABC", "myUid = ${UserVars.myUid}")
                    UserVars.myUid = UserVars.auth.currentUser!!.uid
                    mainVm.curUid = UserVars.myUid
                    mainVm.getUserEmail(mainVm.curUid!!)
                    Log.d("ABC", "curUid = ${mainVm.curUid}")
                    replaceFragment(UserAccountFragment())
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener true
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main_content, fragment)
            .commit()
    }
}