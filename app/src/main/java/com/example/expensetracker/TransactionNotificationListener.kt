package com.example.expensetracker

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class TransactionNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

        val fullText = "$title $text $bigText".trim()
        val packageName = sbn.packageName

        Log.d("NOTIF_FULL", fullText)

        if (
            !packageName.contains("paytm", true) &&
            !packageName.contains("phonepe", true) &&
            !packageName.contains("google", true)
        ) return

        if (!isTransactionText(fullText)) return

        val amount = extractAmount(fullText)
        if (amount <= 0.0) return

        // ✅ FIXED TYPE LOGIC
        val type =
            if (
                fullText.contains("debited", true) ||
                fullText.contains("paid", true) ||
                fullText.contains("spent", true)
            ) {
                "DEBIT"
            } else {
                "CREDIT"
            }

        // ✅ FIXED MERCHANT EXTRACTION
        val merchant = extractMerchant(fullText)

        Log.d("PARSED", "Amount=$amount Type=$type Merchant=$merchant")

        val expense = ExpenseEntity(
            amount = amount,
            merchant = merchant,
            type = type,
            source = "NOTIFICATION",
            timestamp = System.currentTimeMillis()
        )

        ExpenseDatabase
            .getDatabase(applicationContext)
            .expenseDao()
            .insertExpenseFromReceiver(expense)
    }

    /* ---------------- HELPERS ---------------- */

    private fun extractAmount(text: String): Double {
        val regex = Regex(
            "(₹|rs\\.?|inr)\\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\\.\\d{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        val match = regex.find(text)
        return match
            ?.groupValues
            ?.get(2)
            ?.replace(",", "")
            ?.toDoubleOrNull()
            ?: 0.0
    }

    // ✅ WORKS FOR: "Received ₹500 from Veena Francis Dsouza"
    private fun extractMerchant(text: String): String {

        val regex = Regex(
            "(from|to)\\s+([A-Za-z ]+?)(?=\\s(deposited|credited|debited|account|rs|₹|inr|\\.|$))",
            RegexOption.IGNORE_CASE
        )

        val match = regex.find(text)

        return match
            ?.groupValues
            ?.get(2)
            ?.trim()
            ?: "Unknown"
    }



    private fun isTransactionText(text: String): Boolean {
        val keywords = listOf(
            "debited",
            "credited",
            "paid",
            "received",
            "deposited",
            "spent",
            "₹",
            "rs",
            "inr"
        )
        return keywords.any { text.lowercase().contains(it) }
    }
}
