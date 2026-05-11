package com.moneytracker.core.database.local

import com.moneytracker.core.database.dao.ExpenseAccountDao
import com.moneytracker.core.database.dao.ExpenseCategoryDao
import com.moneytracker.core.database.dao.ExpenseDao
import com.moneytracker.core.database.model.ExpenseAccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.database.model.toDomain
import com.moneytracker.core.database.model.toEntity
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseLocalDataSource internal constructor(
    private val expenseAccountDao: ExpenseAccountDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val expenseDao: ExpenseDao
) {
    fun observeExpenses(): Flow<List<Expense>> =
        expenseDao.observeExpenses().map { expenses ->
            expenses.map { expense -> expense.toDomain() }
        }

    fun observeExpenseAccounts(): Flow<List<ExpenseAccount>> =
        expenseAccountDao.observeExpenseAccounts().map { accounts ->
            accounts.map { account -> account.toDomain() }
        }

    fun observeCategories(): Flow<List<ExpenseCategory>> =
        expenseCategoryDao.observeCategories().map { categories ->
            categories.map { category -> category.toDomain() }
        }

    suspend fun nextExpenseId(): ExpenseId =
        ExpenseId(expenseDao.getMaxExpenseId() + 1)

    suspend fun nextExpenseAccountId(): ExpenseAccountId =
        ExpenseAccountId(expenseAccountDao.getMaxExpenseAccountId() + 1)

    suspend fun nextExpenseCategoryId(): ExpenseCategoryId =
        ExpenseCategoryId(expenseCategoryDao.getMaxExpenseCategoryId() + 1)

    suspend fun upsertExpense(expense: Expense) {
        expenseDao.upsertExpense(expense.toEntity())
    }

    suspend fun upsertExpenseAccount(account: ExpenseAccount) {
        expenseAccountDao.upsertExpenseAccount(
            ExpenseAccountEntity(
                id = account.id.value,
                name = account.name
            )
        )
    }

    suspend fun upsertCategory(category: ExpenseCategory) {
        expenseCategoryDao.upsertCategory(
            ExpenseCategoryEntity(
                id = category.id.value,
                name = category.name,
                colorArgb = category.color.argb
            )
        )
    }
}
