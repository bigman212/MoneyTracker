package com.moneytracker.core.domain.repository

import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseId
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>

    suspend fun addExpense(expense: Expense): ExpenseId
}
