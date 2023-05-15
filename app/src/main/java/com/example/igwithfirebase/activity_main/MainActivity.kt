package com.example.igwithfirebase.activity_main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.igwithfirebase.R
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.activity_main.act_post.PostActivity
import com.example.igwithfirebase.activity_main.frag_alarm.FavAlarmFragment
import com.example.igwithfirebase.activity_main.frag_gallery.SearchGalleryFragment
import com.example.igwithfirebase.activity_main.frag_home.HomeFeedFragment
import com.example.igwithfirebase.activity_main.frag_user.UserAccountFragment
import com.example.igwithfirebase.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val mainVm by viewModels<MainViewModel>()  // ViewModel 초기화

    // 갤러리 접근 권한 받아오기
    private val galleryResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            when (it) {
                true ->
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                false ->
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    val permResult = object: PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, PostActivity::class.java))
        }
        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainVm.createUserFollowDtoOrNot()

        setToolbar()
        setNavigation()
        binding.bottomNav.selectedItemId = R.id.nav_account

        /* 앨범 접근 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // showRationaleDialog() not working
        }
        else { // ask for permission
            readPermResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        */
        //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        //permCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        /*
        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")
        val profileUrl = intent.getStringExtra("profile")
        with(binding) {
            tvEmail.text = email
            tvName.text = displayName
            ivProfile.load(profileUrl)
        }


        binding.btnSignout.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        */
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {
                        //TODO: showRationaleDialog() not working
                    } else { // ask for permission
                        //galleryResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        TedPermission.create()
                            .setPermissionListener(permResult)
                            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check()
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.nav_fav_alarm -> {
                    replaceFragment(FavAlarmFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_account -> {
                    mainVm.curUid = UserVars.myUid
                    mainVm.getUserEmail()
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

    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.size > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("STARBUCKS", "You finally got the permission!")
                    startActivity(Intent(this, PostActivity::class.java))
                }
            }
            else -> {
                Log.d("STARBUCKS", "You, again, failed to get permission!")
            }
        }
    }
    */
}