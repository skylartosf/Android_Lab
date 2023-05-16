package com.example.igwithfirebase.activity_main.frag_home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.igwithfirebase.Variables.MyCallback
import com.example.igwithfirebase.Variables.UserVars
import com.example.igwithfirebase.Variables.getCurMyPosts
import com.example.igwithfirebase.Variables.getFollowings
import com.example.igwithfirebase.activity_main.MainViewModel
import com.example.igwithfirebase.databinding.FragmentHomeFeedBinding
import com.example.igwithfirebase.model.PostDTO

class HomeFeedFragment : Fragment() {
    private var _binding: FragmentHomeFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: HomeFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeFeedBinding.inflate(inflater, container, false)

        myAdapter = HomeFeedAdapter()
        binding.rvHomeFeed.adapter = myAdapter

        getPosts()

        return binding.root
    }

    private fun getPosts() {
        getFollowings(UserVars.myUid, object: MyCallback() {
            override fun afterGettingFollowings(list: List<String>) {
                super.afterGettingFollowings(list)
                Log.d("ABC", "You follow: <$list>")
                getCurMyPosts(list, object: MyCallback() {
                    override fun getMyPosts(list: List<PostDTO>) {
                        super.getMyPosts(list)
                        Log.d("ABC", "You've got <${list.size}> posts to display on your home feed")
                        myAdapter.submitList(list)
                    } // [def] getMyPosts
                })
            } // [def] afterGettingFollowings
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}