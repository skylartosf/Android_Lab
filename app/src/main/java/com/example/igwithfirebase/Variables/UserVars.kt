package com.example.igwithfirebase.Variables

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object UserVars {
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var myUid: String? = null
}