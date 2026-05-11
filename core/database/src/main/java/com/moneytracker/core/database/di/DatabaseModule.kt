package com.moneytracker.core.database.di

import android.content.Context
import androidx.room.Room
import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.MoneyTrackerDatabaseCallback
import com.moneytracker.core.database.local.ExpenseLocalDataSource
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
            .addCallback(MoneyTrackerDatabaseCallback(context))
            .build()

    @Provides
    fun provideExpenseLocalDataSource(database: MoneyTrackerDatabase): ExpenseLocalDataSource =
        ExpenseLocalDataSource(
            expenseAccountDao = database.expenseAccountDao(),
            expenseCategoryDao = database.expenseCategoryDao(),
            expenseDao = database.expenseDao()
        )
}
