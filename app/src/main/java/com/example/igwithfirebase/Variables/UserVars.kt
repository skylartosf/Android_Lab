package com.example.igwithfirebase.Variables

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object UserVars {
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var myUid: String
}