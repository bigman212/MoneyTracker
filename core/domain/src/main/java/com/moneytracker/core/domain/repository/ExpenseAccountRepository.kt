package com.moneytracker.core.domain.repository

import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import kotlinx.coroutines.flow.Flow

interface ExpenseAccountRepository {
    fun observeExpenseAccounts(): Flow<List<ExpenseAccount>>

    suspend fun addExpenseAccount(name: String): ExpenseAccountId
}
