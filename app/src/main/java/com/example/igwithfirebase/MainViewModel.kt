package com.example.igwithfirebase

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class MainViewModel: ViewModel() {
    var auth = FirebaseAuth.getInstance()

    var user: FirebaseUser? = auth.currentUser
    var firestore: FirebaseFirestore? = FirebaseFirestore.getInstance()
    var imgSnapshot: ListenerRegistration? = null
}