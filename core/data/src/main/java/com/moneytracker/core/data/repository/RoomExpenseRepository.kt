package com.moneytracker.core.data.repository

import com.moneytracker.core.database.local.ExpenseLocalDataSource
import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import com.moneytracker.core.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RoomExpenseRepository @Inject constructor(
    private val localDataSource: ExpenseLocalDataSource
) : ExpenseRepository {
    override fun observeExpenses(): Flow<List<Expense>> =
        localDataSource.observeExpenses()

    override fun observeExpenseAccounts(): Flow<List<ExpenseAccount>> =
        localDataSource.observeExpenseAccounts()

    override fun observeCategories(): Flow<List<ExpenseCategory>> =
        localDataSource.observeCategories()

    override suspend fun addExpense(expense: Expense): ExpenseId {
        val id = localDataSource.nextExpenseId()
        localDataSource.upsertExpense(expense.copy(id = id))
        return id
    }

    override suspend fun addExpenseAccount(name: String): ExpenseAccountId {
        val id = localDataSource.nextExpenseAccountId()
        localDataSource.upsertExpenseAccount(
            ExpenseAccount(
                id = id,
                name = name
            )
        )
        return id
    }

    override suspend fun addCategory(name: String, color: CategoryColor): ExpenseCategoryId {
        val id = localDataSource.nextExpenseCategoryId()
        localDataSource.upsertCategory(
            ExpenseCategory(
                id = id,
                name = name,
                color = color
            )
        )
        return id
    }
}
