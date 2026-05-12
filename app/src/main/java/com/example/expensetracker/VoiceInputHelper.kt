package com.example.expensetracker

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import java.util.Locale

object VoiceInputHelper {

    const val VOICE_REQUEST_CODE = 200

    fun startVoiceInput(activity: Activity) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Say your expense..."
        )

        try {
            activity.startActivityForResult(intent, VOICE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                "Voice input not supported",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}