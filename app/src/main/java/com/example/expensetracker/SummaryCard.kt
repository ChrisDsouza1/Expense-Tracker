package com.example.expensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummaryCard(expenses: List<ExpenseEntity>) {

    val totalSpent = expenses
        .filter { it.type == "DEBIT" }
        .sumOf { it.amount }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "₹$totalSpent",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
