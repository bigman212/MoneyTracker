package com.moneytracker.core.data.local

import com.moneytracker.core.database.dao.AccountDao
import com.moneytracker.core.database.dao.ExpenseCategoryDao
import com.moneytracker.core.database.dao.ExpenseDao
import com.moneytracker.core.database.model.AccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.database.model.ExpenseEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExpenseLocalDataSource @Inject constructor(
    private val accountDao: AccountDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val expenseDao: ExpenseDao
) {
    fun observeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeExpenses()

    fun observeAccounts(): Flow<List<AccountEntity>> = accountDao.observeAccounts()

    fun observeCategories(): Flow<List<ExpenseCategoryEntity>> =
        expenseCategoryDao.observeCategories()

    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)

    suspend fun insertAccount(account: AccountEntity): Long = accountDao.insertAccount(account)

    suspend fun insertCategory(category: ExpenseCategoryEntity): Long =
        expenseCategoryDao.insertCategory(category)
}
