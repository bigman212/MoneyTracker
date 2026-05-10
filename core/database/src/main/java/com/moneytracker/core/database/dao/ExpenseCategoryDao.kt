package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ExpenseCategoryDao {
    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun observeCategories(): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT COALESCE(MAX(id), 0) FROM expense_categories")
    suspend fun getMaxExpenseCategoryId(): Long

    @Upsert
    suspend fun upsertCategory(category: ExpenseCategoryEntity)
}
