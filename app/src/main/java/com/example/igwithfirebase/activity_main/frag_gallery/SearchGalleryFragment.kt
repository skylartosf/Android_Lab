package com.example.igwithfirebase.activity_main.frag_gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.databinding.FragmentSearchGalleryBinding
import com.example.igwithfirebase.model.PostDTO
import com.google.firebase.firestore.Query

// TODO: UserAccountAdapter이랑 완전 똑같애서 하나만 써도 되겠다..
class SearchGalleryFragment : Fragment() {
    private var _binding: FragmentSearchGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: SearchGalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchGalleryBinding.inflate(inflater, container, false)

        myAdapter = SearchGalleryAdapter()
        binding.rv.adapter = myAdapter

        getEveryFeed()

        return binding.root
    }

    private fun getEveryFeed() {
        var result = mutableListOf<PostDTO>()
        UserVars.firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { queryResult ->
                for (doc in queryResult.documents) { // 각 post를 살펴본다, 해당 uid가 uids 중 하나인지.
                    result.add(doc.toObject(PostDTO::class.java)!!)
                }
                myAdapter.submitList(result)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}