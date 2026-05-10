package com.moneytracker.core.domain.model

import java.time.Instant

data class Expense(
    val id: ExpenseId,
    val name: String,
    val categoryId: ExpenseCategoryId,
    val spentAt: Instant,
    val accountId: AccountId
)

@JvmInline
value class ExpenseId(val value: Long)
