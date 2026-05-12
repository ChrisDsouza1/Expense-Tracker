package com.example.expensetracker

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun DashboardScreen(
    expenses: List<ExpenseEntity>,
    onVoiceClick: () -> Unit
) {
    val context = LocalContext.current
    val (debit, credit) = getTodayTotals(expenses)
    val (weekDebit, weekCredit) = getWeeklyTotals(expenses)

    Scaffold(
        topBar = {
            Text(
                text = "Expense Tracker",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                SummaryCard(expenses)
            }

            item {
                DailySummaryPie(debit, credit)
            }

            item {
                WeeklySummaryPie(weekDebit, weekCredit)
            }

            item {
                Button(
                    onClick = onVoiceClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎤 Add Expense by Voice")
                }
            }

            item {
                Button(
                    onClick = {
                        val intent = Intent(
                            android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔔 Enable Notification Access")
                }
            }

            item {
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(expenses) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}


/* ===================== HELPERS ===================== */

fun getTodayTotals(expenses: List<ExpenseEntity>): Pair<Double, Double> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis

    val today = expenses.filter { it.timestamp >= start }

    val debit = today.filter { it.type == "DEBIT" }.sumOf { it.amount }
    val credit = today.filter { it.type == "CREDIT" }.sumOf { it.amount }

    return debit to credit
}

fun getWeeklyTotals(expenses: List<ExpenseEntity>): Pair<Double, Double> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis

    val week = expenses.filter { it.timestamp >= start }

    val debit = week.filter { it.type == "DEBIT" }.sumOf { it.amount }
    val credit = week.filter { it.type == "CREDIT" }.sumOf { it.amount }

    return debit to credit
}

@Composable
fun DailySummaryPie(debit: Double, credit: Double) {
    val total = debit + credit
    if (total == 0.0) {
        Text("No transactions today")
        return
    }

    val debitAngle = (debit / total * 360f).toFloat()
    val creditAngle = (credit / total * 360f).toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Today's Summary", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(12.dp))

        Canvas(modifier = Modifier.size(180.dp)) {
            drawArc(
                color = Color.Red,
                startAngle = 0f,
                sweepAngle = debitAngle,
                useCenter = true
            )
            drawArc(
                color = Color.Green,
                startAngle = debitAngle,
                sweepAngle = creditAngle,
                useCenter = true
            )
        }

        Spacer(Modifier.height(8.dp))

        Text("🔴 Debit ₹$debit")
        Text("🟢 Credit ₹$credit")
    }
}
@Composable
fun WeeklySummaryPie(debit: Double, credit: Double) {

    val total = debit + credit
    if (total == 0.0) {
        Text("No transactions this week")
        return
    }

    val debitAngle = (debit / total * 360f).toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text("Last 7 Days Summary", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(modifier = Modifier.size(180.dp)) {
            drawArc(Color(0xFFE57373), 0f, debitAngle, true)
            drawArc(Color(0xFF81C784), debitAngle, 360f - debitAngle, true)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("🔴 Debit ₹$debit    🟢 Credit ₹$credit")
    }
}
@Composable
fun SummaryCard(title: String, debit: Double, credit: Double) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Debit: ₹$debit")
            Text("Credit: ₹$credit")
        }
    }
}
