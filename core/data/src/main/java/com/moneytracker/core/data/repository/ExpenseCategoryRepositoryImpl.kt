package com.moneytracker.core.data.repository

import com.moneytracker.core.database.local.ExpenseLocalDataSource
import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.repository.ExpenseCategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ExpenseCategoryRepositoryImpl @Inject constructor(
    private val localDataSource: ExpenseLocalDataSource
) : ExpenseCategoryRepository {
    override fun observeCategories(): Flow<List<ExpenseCategory>> =
        localDataSource.observeCategories()

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
