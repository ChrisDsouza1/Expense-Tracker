package com.example.expensetracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpenseFromReceiver(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun observeAllExpenses(): Flow<List<ExpenseEntity>>

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
}
