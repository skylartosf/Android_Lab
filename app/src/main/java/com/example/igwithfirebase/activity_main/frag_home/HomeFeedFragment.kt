package com.example.igwithfirebase.activity_main.frag_home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.igwithfirebase.activity_main.MainViewModel
import com.example.igwithfirebase.databinding.FragmentHomeFeedBinding

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