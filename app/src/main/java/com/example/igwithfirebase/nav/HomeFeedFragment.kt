package com.example.igwithfirebase.nav

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.igwithfirebase.R

class HomeFeedFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFeedFragment()
    }

    private lateinit var viewModel: HomeFeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeFeedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}