package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.moneytracker.core.database.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY spent_at_epoch_millis DESC, id DESC")
    fun observeExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(MAX(id), 0) FROM expenses")
    suspend fun getMaxExpenseId(): Long

    @Upsert
    suspend fun upsertExpense(expense: ExpenseEntity)
}
