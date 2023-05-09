package com.example.igwithfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.igwithfirebase.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import java.util.Arrays

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication 관리 class
    private lateinit var auth: FirebaseAuth

    // 구글 로그인 관리 클래스
    private lateinit var googleSignInClient: GoogleSignInClient
    // 페이스북 로그인 처리 결과 관리 클래스
    private lateinit var callbackManager: CallbackManager
    // 트위터 로그인
    private lateinit var provider: OAuthProvider.Builder

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

        // for 페이스북
        callbackManager = CallbackManager.Factory.create()

        // for 트위터
        provider = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "kor")

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
        auth.createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPw.text.toString())
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) { // 아이디 생성이 성공했을 경우
                    Toast.makeText(this, R.string.signup_complete, Toast.LENGTH_SHORT).show()
                    moveToMainPage(auth.currentUser) // 다음 페이지 호출
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
        auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPw.text.toString())
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) { // 로그인 성공
                    moveToMainPage(auth.currentUser)
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
                moveToMainPage(auth.currentUser)
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
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    /* 02 : 구글 로그인 - finish */

    /* 03 : 페이스북 로그인 - start */

    private fun facebookLogin() {
        binding.progressBar.visibility = View.VISIBLE
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("FACEBOOK", "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }
            override fun onCancel() {
                Log.d("FACEBOOK", "facebook:onCancel")
            }
            override fun onError(error: FacebookException?) {
                Log.d("FACEBOOK", "facebook:onError", error)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FACEBOOK", "facebook:handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) {
                    Log.d("FACEBOOK", "signInWithCredential:success")
                    moveToMainPage(auth.currentUser)
                }
                else {
                    Log.w("FACEBOOK", "signInWithCredential:fail", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    /* 03 : 페이스북 로그인 - finish */

    private fun twitterLogin() {
        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    Log.d("TWITTER", "twitterLogin():success")
                    moveToMainPage(auth.currentUser)
                }
                .addOnFailureListener {
                    Log.d("TWITTER", "twitterLogin():fail - $it")
                }
        }
        else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            signInWithProvider(provider)
        }
    }

    private fun signInWithProvider(provider: OAuthProvider.Builder) {
        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                Log.d("TWITTER", "signInWithProvider():success")
                moveToMainPage(auth.currentUser)
            }
            .addOnFailureListener {
                Log.d("TWITTER", "signInWithProvider():fail - $it")
            }
    }

    private fun moveToMainPage(user: FirebaseUser?) {
        // user is signed in
        if (user != null) {
            Toast.makeText(this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show()
            val intent: Intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", user.email)
            intent.putExtra("name", user.displayName)
            intent.putExtra("profile", user.photoUrl.toString())
            startActivity(intent)
            finish()
        }
    }
}