package com.example.expensetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SMS_RECEIVER", "Receiver entered");

        if (intent == null ||
                !"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        String format = bundle.getString("format");

        for (Object pdu : pdus) {

            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            String message = sms.getMessageBody();

            Log.d("SMS_RECEIVED", message);

            if (!isTransactionSMS(message)) continue;

            // ---------- AMOUNT ----------
            double amount = extractAmount(message);
            Pattern ap = Pattern.compile("(INR|Rs\\.?)[ ]?([0-9,]+\\.?[0-9]*)");
            Matcher am = ap.matcher(message);
            if (am.find()) {
                amount = Double.parseDouble(am.group(2).replace(",", ""));
            }

            // ---------- MERCHANT ----------
            String merchant = "Unknown";
            Pattern mp = Pattern.compile("Transferred to ([A-Za-z. ]+?)\\.");
            Matcher mm = mp.matcher(message);
            if (mm.find()) {
                merchant = mm.group(1).trim();
            }

            String type = message.toLowerCase().contains("debited") ? "DEBIT" : "CREDIT";

            ExpenseEntity expense = new ExpenseEntity(
                    0,
                    amount,
                    merchant,
                    type,
                    "SMS",
                    System.currentTimeMillis()
            );

            ExpenseDatabase
                    .getDatabase(context)
                    .expenseDao()
                    .insertExpenseFromReceiver(expense);

            Log.d("DB_SAVE_SMS", "SMS expense inserted");
        }
    }
    private double extractAmount(String text) {
        Pattern p = Pattern.compile(
                "(₹|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\\.\\d{1,2})?)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(2).replace(",", ""));
            } catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }


    private boolean isTransactionSMS(String msg) {
        String[] keys = {"debited", "credited", "INR", "Rs"};
        for (String k : keys) {
            if (msg.toLowerCase().contains(k.toLowerCase())) return true;
        }
        return false;
    }
}
