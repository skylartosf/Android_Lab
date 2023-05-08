package com.example.igwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.igwithfirebase.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication 관리 class
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 로그인 통합 관리하는 Object 생성
        auth = FirebaseAuth.getInstance()

        // 로그인 버튼들 세팅
        with(binding) {
            btnLoginEmail.setOnClickListener { emailLogin() }
            btnLoginGoogle.setOnClickListener { googleLogin() }
            btnLoginFacebook.setOnClickListener { facebookLogin() }
            btnLoginTwitter.setOnClickListener { twitterLogin() }
        }
    }

    private fun emailLogin() {
        if (binding.etEmail.text.toString().isNullOrEmpty() || binding.etPw.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, R.string.signout_fail_null, Toast.LENGTH_SHORT).show()
        }
        else {
            binding.progressBar.visibility = View.VISIBLE
            createAndLoginEmail()
        }
    }

    // 이메일 회원 가입 및 로그인 메소드
    private fun createAndLoginEmail() {
        auth?.createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPw.text.toString())
            ?.addOnCompleteListener { task ->
                binding.progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) { // 아이디 생성이 성공했을 경우
                    Toast.makeText(this, R.string.signup_complete, Toast.LENGTH_SHORT).show()
                    moveToMainPage(auth?.currentUser) // 다음 페이지 호출
                }
                else if (task.exception?.message.isNullOrEmpty()) { // 회원가입 에러가 발생했을 경우
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
                else {
                    signinEmail()
                }
            }
    }

    // 이메일 로그인 메소드
    private fun signinEmail() {
        auth?.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPw.text.toString())
            ?.addOnCompleteListener { task ->
                binding.progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) { // 로그인 성공
                    moveToMainPage(auth?.currentUser)
                }
                else { // 로그인 실패
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun googleLogin() {
        TODO("Not yet implemented")
    }

    private fun facebookLogin() {
        TODO("Not yet implemented")
    }

    private fun twitterLogin() {
        TODO("Not yet implemented")
    }

    private fun moveToMainPage(user: FirebaseUser?) {
        // user is signed in
        if (user != null) {
            Toast.makeText(this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}