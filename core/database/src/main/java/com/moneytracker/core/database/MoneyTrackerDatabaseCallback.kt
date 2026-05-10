package com.moneytracker.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class MoneyTrackerDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO expense_accounts(id, name) VALUES (1, 'Основной')")
        db.execSQL(
            "INSERT INTO expense_categories(id, name, color_argb) VALUES " +
                "(1, 'Продукты', 4278255360), " +
                "(2, 'Рестораны', 4294901760), " +
                "(3, 'Такси', 4294944000)"
        )
    }
}
