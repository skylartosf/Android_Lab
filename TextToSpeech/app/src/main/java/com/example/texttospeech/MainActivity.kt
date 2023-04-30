package com.example.texttospeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.texttospeech.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        binding.btn.setOnClickListener {
            if (binding.et.text!!.isEmpty()) {
                Toast.makeText(this@MainActivity,
                "Enter a text to speak!", Toast.LENGTH_SHORT).show()
            }
            else {
                speakOut(binding.et.text.toString())
            }
        }

        binding.btnStop.setOnClickListener {
            tts?.stop()
        }
    }

    // call to signal the completion of TTS engine initialization
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) { // if the language is not supported
                Log.e("TTS", "The language specified is not supported")
            }
        }
        else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "")
        // QUEUE_FLUSH: whatever said earlier in TTS will be deleted
        // press btn repeatedly -> what's said earlier is cut and new one is said
        // QUEUE_ADD: add the new thing that we want to speak to what has been already spoken
        // press btn repeatedly -> after what's said earlier is said, then new one is said
        // utteranceId: sth like comment, remark, expression, statements..
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
    }
}