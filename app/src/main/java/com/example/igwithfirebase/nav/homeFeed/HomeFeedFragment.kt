package com.example.igwithfirebase.nav.homeFeed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.igwithfirebase.MainViewModel
import com.example.igwithfirebase.R
import com.example.igwithfirebase.databinding.ActivityLoginBinding
import com.example.igwithfirebase.databinding.DialogUploadLoadingBinding
import com.example.igwithfirebase.databinding.FragmentHomeFeedBinding
import com.example.igwithfirebase.databinding.ItemHomeFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFeedFragment : Fragment() {
    private var _binding: FragmentHomeFeedBinding? = null
    private val binding get() = _binding!!
    private val mainVm: MainViewModel by activityViewModels() // ViewModel 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        _binding?.rvHomeFeed?.adapter = HomeFeedAdapter(
            mainVm.myUid,
            null,
            mainVm.theirPosts,
            mainVm.theirPostUids
        )
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}