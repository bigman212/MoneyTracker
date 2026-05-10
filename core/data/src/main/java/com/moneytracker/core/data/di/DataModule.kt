package com.moneytracker.core.data.di

import com.moneytracker.core.data.repository.RoomExpenseRepository
import com.moneytracker.core.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindExpenseRepository(repository: RoomExpenseRepository): ExpenseRepository
}
