package com.example.a7mworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a7mworkoutapp.databinding.ActivityExerciseBinding
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityExerciseBinding

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exTimer: CountDownTimer? = null
    private var exProgress = 0

    private lateinit var exList: ArrayList<ExerciseModel>
    private var curExPos = -1

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbEx)

        if (supportActionBar != null) { // activate back button on toolbar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

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

            tvUpcomingExLabel.visibility = View.VISIBLE
            tvUpcomingEx.visibility = View.VISIBLE
            tvUpcomingEx.text = exList[curExPos + 1].getName()
        }

        speakOut("Take rest for 10 seconds")

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        setRestPb()
    }

    private fun setRestPb() {
        binding.pb.progress = restProgress
        restTimer = object: CountDownTimer(1000, 1000) {
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

            tvUpcomingExLabel.visibility = View.GONE
            tvUpcomingEx.visibility = View.GONE
        }

        if (exTimer != null) {
            exTimer?.cancel()
            exProgress = 0
        }

        speakOut(exList[curExPos].getName())

        with(binding) {
            ivImg.setImageResource(exList[curExPos].getImage())
            tvEx.text = exList[curExPos].getName()
        }
        setExPb()
    }

    private fun setExPb() {
        binding.exPb.progress = exProgress
        exTimer = object: CountDownTimer(1000, 1000) {
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
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exTimer != null) {
            exTimer?.cancel()
            exProgress = 0
        }
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        //binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US) // set US Eng as language for tts
            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language specified is not suported!")
            }
        }
        else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}