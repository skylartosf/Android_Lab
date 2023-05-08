package com.example.igwithfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.igwithfirebase.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication 관리 class
    private lateinit var auth: FirebaseAuth

    // 구글 로그인 관리 클래스
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 로그인 통합 관리하는 Object 생성
        auth = FirebaseAuth.getInstance()

        // 구글 로그인 옵션
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // google-services.json가 자동으로 default_web_client_id라는 string 추가해줌
            .requestEmail()
            .build()

        // 구글 로그인 클래스를 만듦
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 로그인 버튼들 세팅
        with(binding) {
            btnLoginEmail.setOnClickListener { emailLogin() }
            btnLoginGoogle.setOnClickListener { googleLogin() }
            btnLoginFacebook.setOnClickListener { facebookLogin() }
            btnLoginTwitter.setOnClickListener { twitterLogin() }
        }
    }

    /* 01 : 이메일 로그인 - start */

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

    /* 01 : 이메일 로그인 - finish */

    /* 02 : 구글 로그인 - start */

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        }
        else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                moveToMainPage(auth?.currentUser)
            }
            else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun googleLogin() {
        binding.progressBar.visibility = View.VISIBLE

        val gsa: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (gsa != null) { // 기존에 로그인 했던 계정이 남아있으면
            googleSignInClient.signOut() // 그거 로그아웃 -> 그래야 다른 구글 계정도 선택 가능
        }
        val signInIntent = googleSignInClient?.signInIntent
        launcher.launch(signInIntent)
    }

    /* 02 : 구글 로그인 - finish */

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
            val intent: Intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", user.email)
            intent.putExtra("name", user.displayName)
            startActivity(intent)
            finish()
        }
    }
}