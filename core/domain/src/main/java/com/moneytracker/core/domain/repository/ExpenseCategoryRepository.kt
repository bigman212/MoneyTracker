package com.moneytracker.core.domain.repository

import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import kotlinx.coroutines.flow.Flow

interface ExpenseCategoryRepository {
    fun observeCategories(): Flow<List<ExpenseCategory>>

    suspend fun addCategory(name: String, color: CategoryColor): ExpenseCategoryId
}
