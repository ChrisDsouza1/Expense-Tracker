package com.example.expensetracker

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.ui.theme.ExpensetrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Runtime permissions
        if (checkSelfPermission(Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECORD_AUDIO
                ),
                100
            )
        }

        Log.d(
            "PERMISSION_CHECK",
            "READ_SMS = " + (checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED)
        )

        enableEdgeToEdge()

        setContent {
            ExpensetrackerTheme {

                val context = LocalContext.current

                // ✅ UI FIX: Observe DB continuously using Flow
                val expenses by ExpenseDatabase
                    .getDatabase(context)
                    .expenseDao()
                    .observeAllExpenses()
                    .collectAsState(initial = emptyList())

                DashboardScreen(
                    expenses = expenses,
                    onVoiceClick = {
                        VoiceInputHelper.startVoiceInput(context as Activity)
                    }
                )
            }
        }
    }

    // ⚠️ Deprecated but still works (we'll modernize later)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VoiceInputHelper.VOICE_REQUEST_CODE &&
            resultCode == RESULT_OK &&
            data != null
        ) {
            val results =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            if (!results.isNullOrEmpty()) {
                val spokenText = results[0]
                Log.d("VOICE_INPUT", "Recognized: $spokenText")
                parseVoiceExpense(spokenText)
            }
        }
    }

    private fun parseVoiceExpense(text: String) {

        val lowerText = text.lowercase()

        // 💰 Amount
        val amountRegex = "(\\d+(?:\\.\\d+)?)".toRegex()
        val amountMatch = amountRegex.find(lowerText)
        val amount = amountMatch?.value?.toDoubleOrNull() ?: 0.0

        // 🏪 Merchant
        var merchant = "Unknown"
        val merchantRegex = "(to|for)\\s([a-zA-Z ]+)".toRegex()
        val merchantMatch = merchantRegex.find(lowerText)
        if (merchantMatch != null) {
            merchant = merchantMatch.groupValues[2].trim()
        }

        val expense = ExpenseEntity(
            amount = amount,
            merchant = merchant,
            type = "DEBIT",
            source = "VOICE",
            timestamp = System.currentTimeMillis()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            ExpenseDatabase
                .getDatabase(this@MainActivity)
                .expenseDao()
                .insertExpense(expense)

            Log.d("DB_SAVE", "Voice expense inserted")

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity,
                    "Voice expense saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
