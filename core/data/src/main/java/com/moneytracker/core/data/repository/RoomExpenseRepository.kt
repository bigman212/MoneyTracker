package com.moneytracker.core.data.repository

import com.moneytracker.core.data.local.ExpenseLocalDataSource
import com.moneytracker.core.data.model.asDomain
import com.moneytracker.core.data.model.asEntity
import com.moneytracker.core.database.model.AccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.domain.model.Account
import com.moneytracker.core.domain.model.AccountId
import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import com.moneytracker.core.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomExpenseRepository @Inject constructor(
    private val localDataSource: ExpenseLocalDataSource
) : ExpenseRepository {
    override fun observeExpenses(): Flow<List<Expense>> =
        localDataSource.observeExpenses().map { expenses ->
            expenses.map { expense -> expense.asDomain() }
        }

    override fun observeAccounts(): Flow<List<Account>> =
        localDataSource.observeAccounts().map { accounts ->
            accounts.map { account -> account.asDomain() }
        }

    override fun observeCategories(): Flow<List<ExpenseCategory>> =
        localDataSource.observeCategories().map { categories ->
            categories.map { category -> category.asDomain() }
        }

    override suspend fun addExpense(expense: Expense): ExpenseId =
        ExpenseId(localDataSource.insertExpense(expense.asEntity()))

    override suspend fun addAccount(name: String): AccountId =
        AccountId(localDataSource.insertAccount(AccountEntity(name = name)))

    override suspend fun addCategory(name: String, color: CategoryColor): ExpenseCategoryId =
        ExpenseCategoryId(
            localDataSource.insertCategory(
                ExpenseCategoryEntity(
                    name = name,
                    colorArgb = color.argb
                )
            )
        )
}
