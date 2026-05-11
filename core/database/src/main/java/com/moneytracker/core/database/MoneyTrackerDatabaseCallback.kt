package com.moneytracker.core.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moneytracker.core.database.R.string.default_expense_account_name
import com.moneytracker.core.database.R.string.default_expense_category_groceries
import com.moneytracker.core.database.R.string.default_expense_category_restaurants
import com.moneytracker.core.database.R.string.default_expense_category_taxi

class MoneyTrackerDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO expense_accounts(id, name) VALUES (1, ?)",
            arrayOf(context.getString(default_expense_account_name))
        )
        db.execSQL(
            "INSERT INTO expense_categories(id, name, color_argb) VALUES " +
                "(1, ?, 4278255360), " +
                "(2, ?, 4294901760), " +
                "(3, ?, 4294944000)",
            arrayOf(
                context.getString(default_expense_category_groceries),
                context.getString(default_expense_category_restaurants),
                context.getString(default_expense_category_taxi)
            )
        )
    }
}
