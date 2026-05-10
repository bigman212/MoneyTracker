package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.moneytracker.core.database.model.ExpenseAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ExpenseAccountDao {
    @Query("SELECT * FROM expense_accounts ORDER BY name ASC")
    fun observeExpenseAccounts(): Flow<List<ExpenseAccountEntity>>

    @Query("SELECT COALESCE(MAX(id), 0) FROM expense_accounts")
    suspend fun getMaxExpenseAccountId(): Long

    @Upsert
    suspend fun upsertExpenseAccount(account: ExpenseAccountEntity)
}
