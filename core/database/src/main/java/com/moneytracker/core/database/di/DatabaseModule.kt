package com.moneytracker.core.database.di

import android.content.Context
import androidx.room.Room
import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.MoneyTrackerDatabaseCallback
import com.moneytracker.core.database.dao.AccountDao
import com.moneytracker.core.database.dao.ExpenseCategoryDao
import com.moneytracker.core.database.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoneyTrackerDatabase =
        Room.databaseBuilder(
            context,
            MoneyTrackerDatabase::class.java,
            "money_tracker.db"
        )
            .addCallback(MoneyTrackerDatabaseCallback())
            .build()

    @Provides
    fun provideAccountDao(database: MoneyTrackerDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideExpenseCategoryDao(database: MoneyTrackerDatabase): ExpenseCategoryDao =
        database.expenseCategoryDao()

    @Provides
    fun provideExpenseDao(database: MoneyTrackerDatabase): ExpenseDao = database.expenseDao()
}
