package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,
    val merchant: String,
    val type: String,      // DEBIT or CREDIT
    val source: String,    // SMS or VOICE
    val timestamp: Long
)
