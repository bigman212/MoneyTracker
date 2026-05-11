package com.moneytracker.main

data class MainScreenUi(
    val balance: String,
    val expenses: List<ExpenseRowUi>
)
