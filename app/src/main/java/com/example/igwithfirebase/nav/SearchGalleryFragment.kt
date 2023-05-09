package com.example.igwithfirebase.nav

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.igwithfirebase.R

class SearchGalleryFragment : Fragment() {

    companion object {
        fun newInstance() = SearchGalleryFragment()
    }

    private lateinit var viewModel: SearchGalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchGalleryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}