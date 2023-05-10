package com.example.igwithfirebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private val permResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                Log.d("STARBUCKS", "You got the permission!")
                startActivity(Intent(this, AddPhotoActivity::class.java))
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                Log.d("STARBUCKS", "You didn't get the permission!")
            }
        }

    private var permCheck = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setToolbar()
        setNavigation()
        binding.bottomNav.selectedItemId = R.id.nav_home

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {
                        //TODO: showRationaleDialog() not working
                    } else { // ask for permission
                        permResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    /*
                    when {
                        ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                            // You can use the API that requires the permission
                            startActivity(Intent(this, AddPhotoActivity::class.java))
                        }
                        shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                           // explain to the user why your app needs this permission
                        }
                        else -> {
                            // You can directly ask for the permission
                            Log.d("permission", "asking for permission!")
                            readPermResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            //Toast.makeText(this, "스토리지 읽기 권한이 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    */
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

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main_content, fragment)
            .commit()
    }

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
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }
            }
            else -> {
                Log.d("STARBUCKS", "You, again, failed to get permission!")
            }
        }
    }
}