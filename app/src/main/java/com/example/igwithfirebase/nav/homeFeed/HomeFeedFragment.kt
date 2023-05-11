package com.example.igwithfirebase.nav.homeFeed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.igwithfirebase.MainViewModel
import com.example.igwithfirebase.R
import com.example.igwithfirebase.databinding.ActivityLoginBinding
import com.example.igwithfirebase.databinding.DialogUploadLoadingBinding
import com.example.igwithfirebase.databinding.FragmentHomeFeedBinding
import com.example.igwithfirebase.databinding.ItemHomeFeedBinding
import com.example.igwithfirebase.nav.model.FollowDTO
import com.example.igwithfirebase.nav.model.PostDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFeedFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentHomeFeedBinding? = null
    private val binding get() = _binding!! // backing proerty

    private var imgSnapshot: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.rvHomeFeed?.adapter = HomeFeedRvAdapter(mainViewModel.firestore)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        imgSnapshot?.remove()
    }

}