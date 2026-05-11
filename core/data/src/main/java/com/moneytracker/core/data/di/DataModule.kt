package com.moneytracker.core.data.di

import com.moneytracker.core.data.repository.ExpenseAccountRepositoryImpl
import com.moneytracker.core.data.repository.ExpenseCategoryRepositoryImpl
import com.moneytracker.core.data.repository.ExpenseRepositoryImpl
import com.moneytracker.core.domain.repository.ExpenseAccountRepository
import com.moneytracker.core.domain.repository.ExpenseCategoryRepository
import com.moneytracker.core.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindExpenseRepository(repository: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    fun bindExpenseAccountRepository(
        repository: ExpenseAccountRepositoryImpl
    ): ExpenseAccountRepository

    @Binds
    fun bindExpenseCategoryRepository(
        repository: ExpenseCategoryRepositoryImpl
    ): ExpenseCategoryRepository
}
