package com.moneytracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneytracker.core.database.dao.ExpenseAccountDao
import com.moneytracker.core.database.dao.ExpenseCategoryDao
import com.moneytracker.core.database.dao.ExpenseDao
import com.moneytracker.core.database.model.ExpenseAccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.database.model.ExpenseEntity

@Database(
    entities = [
        ExpenseAccountEntity::class,
        ExpenseCategoryEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoneyTrackerDatabase : RoomDatabase() {
    internal abstract fun expenseAccountDao(): ExpenseAccountDao

    internal abstract fun expenseCategoryDao(): ExpenseCategoryDao

    internal abstract fun expenseDao(): ExpenseDao
}
