package com.moneytracker.core.data.repository

import com.moneytracker.core.database.local.ExpenseLocalDataSource
import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import com.moneytracker.core.domain.repository.ExpenseAccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExpenseAccountRepositoryImpl @Inject constructor(
    private val localDataSource: ExpenseLocalDataSource
) : ExpenseAccountRepository {
    override fun observeExpenseAccounts(): Flow<List<ExpenseAccount>> =
        localDataSource.observeExpenseAccounts()

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
}
