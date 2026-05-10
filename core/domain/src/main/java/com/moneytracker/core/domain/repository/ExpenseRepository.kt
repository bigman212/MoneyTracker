package com.moneytracker.core.domain.repository

import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>

    fun observeExpenseAccounts(): Flow<List<ExpenseAccount>>

    fun observeCategories(): Flow<List<ExpenseCategory>>

    suspend fun addExpense(expense: Expense): ExpenseId

    suspend fun addExpenseAccount(name: String): ExpenseAccountId

    suspend fun addCategory(name: String, color: CategoryColor): ExpenseCategoryId
}
