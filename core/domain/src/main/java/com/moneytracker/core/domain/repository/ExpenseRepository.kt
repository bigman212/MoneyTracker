package com.moneytracker.core.domain.repository

import com.moneytracker.core.domain.model.Account
import com.moneytracker.core.domain.model.AccountId
import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>

    fun observeAccounts(): Flow<List<Account>>

    fun observeCategories(): Flow<List<ExpenseCategory>>

    suspend fun addExpense(expense: Expense): ExpenseId

    suspend fun addAccount(name: String): AccountId

    suspend fun addCategory(name: String, color: CategoryColor): ExpenseCategoryId
}
