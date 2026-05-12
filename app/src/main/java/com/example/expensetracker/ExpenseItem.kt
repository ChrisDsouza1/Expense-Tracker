package com.example.expensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExpenseItem(expense: ExpenseEntity) {

    val badgeColor = when (expense.source) {
        "SMS" -> Color(0xFF64B5F6)    // Blue
        "VOICE" -> Color(0xFF81C784)  // Green
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = expense.merchant,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = expense.type,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {

                Text(
                    text = "₹${expense.amount}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    color = badgeColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = expense.source,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

