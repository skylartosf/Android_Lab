package com.example.igwithfirebase.nav.homeFeed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
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

    companion object {
        fun newInstance() = HomeFeedFragment()
    }

    private lateinit var viewModel: HomeFeedViewModel

    private var user: FirebaseUser? = null
    private var firestore: FirebaseFirestore? = null
    private var imgSnapshot: ListenerRegistration? = null

    private lateinit var fragBinding: FragmentHomeFeedBinding
    private lateinit var itemBinding: ItemHomeFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()

        fragBinding = FragmentHomeFeedBinding.inflate(layoutInflater)
        itemBinding = ItemHomeFeedBinding.inflate(layoutInflater)

        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onResume() {
        super.onResume()
        fragBinding.rvHomeFeed.layoutManager = LinearLayoutManager(activity)
        fragBinding.rvHomeFeed.adapter = HomeFeedRvAdapter()
    }

    override fun onStop() {
        super.onStop()
        imgSnapshot?.remove()
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeFeedViewModel::class.java)
        // TODO: Use the ViewModel
    }
    */
}