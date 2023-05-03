package com.example.recycler_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recycler_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = Adapter(SampleData.dataList)
        with(binding) {
            rv.adapter = adapter
            //rv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }
}