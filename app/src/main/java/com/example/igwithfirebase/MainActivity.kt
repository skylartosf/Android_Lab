package com.example.igwithfirebase

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.igwithfirebase.databinding.ActivityMainBinding
import com.example.igwithfirebase.nav.AddPhotoActivity
import com.example.igwithfirebase.nav.FavAlarmFragment
import com.example.igwithfirebase.nav.homeFeed.HomeFeedFragment
import com.example.igwithfirebase.nav.SearchGalleryFragment
import com.example.igwithfirebase.nav.UserAccountFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setToolbar()
        setNavigation()
        binding.bottomNav.selectedItemId = R.id.nav_home

        // 앨범 접근 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

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
        setSupportActionBar(binding.myToolbar)
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

                R.id.nav_add_photo -> { // (유일하게) activity 호출
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))
                    } else {
                        Toast.makeText(this, "스토리지 읽기 권한이 없습니다", Toast.LENGTH_SHORT).show()
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.nav_fav_alarm -> {
                    replaceFragment(FavAlarmFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.nav_account -> {
                    val accountFrag = UserAccountFragment()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val bundle = Bundle()
                    bundle.putString("destinationUid", uid)
                    accountFrag.arguments = bundle
                    replaceFragment(accountFrag)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main_content, fragment)
            .commit()
    }
}