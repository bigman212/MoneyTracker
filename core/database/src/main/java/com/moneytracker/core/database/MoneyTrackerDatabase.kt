package com.moneytracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneytracker.core.database.dao.AccountDao
import com.moneytracker.core.database.dao.ExpenseCategoryDao
import com.moneytracker.core.database.dao.ExpenseDao
import com.moneytracker.core.database.model.AccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.database.model.ExpenseEntity

@Database(
    entities = [
        AccountEntity::class,
        ExpenseCategoryEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoneyTrackerDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun expenseCategoryDao(): ExpenseCategoryDao

    abstract fun expenseDao(): ExpenseDao
}
