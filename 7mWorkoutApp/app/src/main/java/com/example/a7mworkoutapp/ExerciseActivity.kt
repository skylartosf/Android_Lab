package com.example.a7mworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.example.a7mworkoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exTimer: CountDownTimer? = null
    private var exProgress = 0

    private var exList: ArrayList<ExerciseModel>? = null
    private var curExPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbEx)

        if (supportActionBar != null) { // activate back button on toolbar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exList = Constants.defaultExerciseList()

        // do the same thing(going back) when you click the back button 'on the device'
        binding.tbEx.setNavigationOnClickListener {
            onBackPressed()
        }

        setupRestView()
    }

    private fun setupRestView() {
        with(binding) {
            flRest.visibility = View.VISIBLE
            tvTitle.visibility = View.VISIBLE
            tvEx.visibility = View.INVISIBLE
            flExercise.visibility = View.INVISIBLE
            ivImg.visibility = View.INVISIBLE
        }

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        setRestPb()
    }

    private fun setRestPb() {
        binding.pb.progress = restProgress
        restTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding.pb.progress = 10 - restProgress
                binding.tvTimer.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                curExPos++
                setupExView()
            }
        }.start()
    }

    private fun setupExView() {
        with(binding) {
            flRest.visibility = View.INVISIBLE
            tvTitle.visibility = View.INVISIBLE
            tvEx.visibility = View.VISIBLE
            flExercise.visibility = View.VISIBLE
            ivImg.visibility = View.VISIBLE
        }

        if (exTimer != null) {
            exTimer?.cancel()
            exProgress = 0
        }

        with(binding) {
            ivImg.setImageResource(exList!![curExPos].getImage())
            tvEx.text = exList!![curExPos].getName()
        }
        setExPb()
    }

    private fun setExPb() {
        binding.exPb.progress = exProgress
        exTimer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exProgress++
                binding.exPb.progress = 30 - exProgress
                binding.tvExTimer.text = (30 - exProgress).toString()
            }
            override fun onFinish() {
                if (curExPos < exList?.size!! - 1) {
                    setupRestView()
                }
                else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congrats! You've completed 7m workout!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        super.onDestroy()
        //binding = null
    }
}