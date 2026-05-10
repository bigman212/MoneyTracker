package com.moneytracker.core.data.repository

import com.moneytracker.core.database.local.ExpenseLocalDataSource
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseId
import com.moneytracker.core.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExpenseRepositoryImpl @Inject constructor(
    private val localDataSource: ExpenseLocalDataSource
) : ExpenseRepository {
    override fun observeExpenses(): Flow<List<Expense>> =
        localDataSource.observeExpenses()

    override suspend fun addExpense(expense: Expense): ExpenseId {
        val id = localDataSource.nextExpenseId()
        localDataSource.upsertExpense(expense.copy(id = id))
        return id
    }
}
