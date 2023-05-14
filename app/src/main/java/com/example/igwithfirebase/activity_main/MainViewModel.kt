package com.example.igwithfirebase.activity_main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.igwithfirebase.Variables.Constants
import com.example.igwithfirebase.model.FollowDTO
import com.example.igwithfirebase.model.PostDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainViewModel: ViewModel() {
    // 툴바에 쓰여질 이름
    private val _tbTitle: MutableLiveData<String> = MutableLiveData()
    val tbTitle: LiveData<String> = _tbTitle

    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var imgSnapshot: ListenerRegistration? = null

    val auth = FirebaseAuth.getInstance()!!
    val myUid = auth.currentUser?.uid!!
    var curUid: String? = null // 현재 위치한 UserAccountFragment의 uid
    var curName: String? = null // curUid의 name(email)

    // 내 프로필 사진이 (있다면) storage 내 저장된 url
    private val _myProfileUrl: MutableLiveData<String> = MutableLiveData()
    val myProfileUrl: LiveData<String> = _myProfileUrl

    private val _queryResult: MutableLiveData<List<DocumentSnapshot>> = MutableLiveData()
    val queryResult: LiveData<List<DocumentSnapshot>> = _queryResult

    var theirPosts = arrayListOf<PostDTO>()
    var theirPostUids = arrayListOf<String>()

    // 로그인 해서 MainActivity로 처음 들어왔을 때
    // fs 내 지금 들어온 유저 정보(FollowDto)가 존재하지 않으면 doc을 하나 생성한다
    fun createUserFollowDtoOrNot() {
        Log.d("STARBUCKS", "I'm inside createUserFollowDtoOrNot()")
        firestore.collection("users")?.document(myUid)?.get()
            ?.addOnSuccessListener {
                if (!it.exists()) {
                    Log.d("STARBUCKS", "You are not in the db. Let me make a follow DTO for you.")
                    firestore.collection("users").document(myUid)
                        .set( FollowDTO(name = auth.currentUser?.email!!) )
                        .addOnSuccessListener {
                            Log.d("STARBUCKS", "You've made a new doc to 'users' collection in fs!")
                        }
                }
            }
    }

    // curUid 유저의 email을 얻는다
    fun getUserEmail() {
        firestore.collection("users")?.document(curUid!!)?.get()
            ?.addOnSuccessListener {
                curName = it.data?.get("name") as String?
                _tbTitle.value = curName // 툴바 title 변경
            }
    }

    // 1. users 에서 '내'가 follow하는 목록을 가져옴 -> 내 팔로잉 목록을 어디에 저장 (내가 팔로잉하는 사람들 uid 목록)
    // 그 follow 목록에 있는 각각의 유저들에 대해서
    // post 있는지 찾아(원본 코드에서 collection("images")
    // -> 그 document가 == querySnapshot
    // 그 querySnapshot들이 '나'의 홈피드에 띄워야 할 post들이니까
    // postDTO에 저장

    // 1. collection("users") : 각 유저의 following, follower 목록이 담겨있다
    // 거기서 내 following 목록을 가져온다 -> 그 목록을 바탕으로 그들이 쓴 post 정보 가져오기
    fun showPostsOnHomeFeed() {
        firestore.collection("users")?.document(myUid!!)?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) { // '나'의 follow 정보(following, follower)를 찾았다
                val myFollowInfo = task.result.toObject(FollowDTO::class.java)
                if (myFollowInfo?.followings != null) { // 그럼 내 following 사람들이 작성한 정보를 찾는다
                    getPostsByMyFollowings(myFollowInfo?.followings)
                }
            }
            else {
                Log.d("STARBUCKS", "Getting all of my followings' names, failed")
            }
        }
    }

    // 내 followings가 쓴 글(postDTO)을 찾아 theirPosts, theirPostUids 구성
    fun getPostsByMyFollowings(iFollow: MutableMap<String, Boolean>?) {
        imgSnapshot = firestore?.collection("posts")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, fbFsException -> // querySnapshot은 전체 게시물(postDTO)을 가져왔다
                theirPosts.clear()
                theirPostUids.clear()
                if (querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) { // 전체 게시물을 하나씩(item) 살펴보는데
                    val item = snapshot.toObject(PostDTO::class.java)!!
                    if (iFollow?.keys?.contains(item.uid)!!) { // 이 item의 작성자 uid가, 내 following 목록에 있다면
                        theirPosts.add(item) // 그 게시물 정보(postDTO)를 theirPosts에 저장
                        theirPostUids.add(snapshot.id) // 그 게시물 정보의 uid를 theirPostUids에 저장
                    }
                }

                // notifyDataSetChanged()
            }
    }

    // 내가 쓴 글들을 찾아 myPosts 구성
    fun getPostsByCurUid() {
        firestore?.collection("posts")
            ?.whereEqualTo("uid", curUid)?.orderBy("timestamp")
            ?.addSnapshotListener { qs, ex ->
                if (qs == null) return@addSnapshotListener
                _queryResult.value = qs.documents
            }
    }

    // [UserAccountFragment] 나의 프로필 사진을 가져온다
    fun getProfile() {
        firestore.collection("profileImages").document(myUid)
            .get().addOnSuccessListener {
                Log.d("STARBUCKS", "I've got...${it.data.toString()}")
                if (it.data != null)
                    _myProfileUrl.value = it.data?.get("imgUrl").toString()
            }
    }

    // [UserAccountFragment] 나의 프로필 사진을 바꾸겠다 (새로운 사진 파일 위치 = uri)
    fun changeProfile(uri: Uri) {
        var filename = myUid + "." + "png"
        var storageRef = storage.reference.child("profileImages/$filename")
        Log.d("STARBUCKS", "[storageRef] $storageRef")

        // 새로운 프로필 사진을 storage에 업로드한다
        uploadImageToStorage(storageRef, uri, Constants.DTO_PROFILE_IMG, null)

        // 안 됐던 거 : 왜 안 됐나 찾아보거라
        /*
        storageRef.putFile(uri)
            .addOnSuccessListener { ts ->
                // 1. 유저는 기존에 프로필 사진이 있었고 그걸 바꾼 것이다
                // -> firestore 내 doc 찾아서 'profileUrl' 필드 갱신
                if (myProfileUrl != null) {
                    myProfileUrl = ts.metadata?.reference?.downloadUrl.toString()
                    firestore.collection("profileImages").document(myUid)
                        .update("profileUrl", myProfileUrl)
                        .addOnSuccessListener {
                            Log.e("STARBUCKS", "You've updated 'profileImages' collection in fs!")
                        }
                }
                // 2. 유저는 기존에 프로필 사진이 없었고 이번이 첨 바꾸는 것이다
                // -> firestore 내 doc 새로이 생성
                else {
                    myProfileUrl = ts.metadata?.reference?.downloadUrl.toString()
                    firestore.collection("profileImages").document(myUid)
                        .set( hashMapOf("profileUrl" to myProfileUrl) )
                        .addOnSuccessListener {
                            Log.e("STARBUCKS", "You've made a new doc to 'profileImages' collection in fs!")
                        }
                }
            }
         */
    }

    // Firebase 내 storageRef 위치로 uri를 업로드한다
    fun uploadImageToStorage(
        storageRef: StorageReference,
        imgUri: Uri?,
        command: String,
        postContent: String?
    ) {
        imgUri?.let {
            // 1. firebase - storage 에 이미지 파일(imgUri) 업로드
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) { // storage에 업로드 성공
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        when(command) {
                            Constants.DTO_POST ->
                                linkUrlToFirestore_post(uri.toString(), postContent!!)
                            Constants.DTO_PROFILE_IMG ->
                                linkUrlToFirestore_profile(uri.toString())
                        }
                    }
                }
                else { // storage에 업로드 실패
                    Log.d("STARBUCKS", "firebase/firestore에 업로드 실패")
                }
            }
        }
    }

    private fun linkUrlToFirestore_post(storageUrl: String, postContent: String) {
        val postDto = PostDTO(
            uid = auth.currentUser?.uid,
            userIdEmail = auth.currentUser?.email,
            timestamp = System.currentTimeMillis(),
            imgUrl = storageUrl,
            content = postContent
        )
        firestore.collection("posts").add(postDto)
            .addOnCompleteListener { fsTask ->
                if (fsTask.isSuccessful) {
                    Log.d("STARBUCKS", "Successfully upload the post.")
                }
                else {
                    Log.d("STARBUCKS", "Failed to upload the post.")
                }
            }
    }

    private fun linkUrlToFirestore_profile(storageUrl: String) {
        var profileDto = hashMapOf("imgUrl" to storageUrl)
        firestore.collection("profileImages").document(myUid).set(profileDto)
            .addOnCompleteListener { fsTask ->
                if (fsTask.isSuccessful) {
                    Log.d("STARBUCKS", "Successfully upload the profile.")
                }
                else {
                    Log.d("STARBUCKS", "Failed to upload the profile.")
                }
            }
    }
}