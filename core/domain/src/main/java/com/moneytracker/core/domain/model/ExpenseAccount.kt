package com.moneytracker.core.domain.model

data class ExpenseAccount(
    val id: ExpenseAccountId,
    val name: String
)

@JvmInline
value class ExpenseAccountId(val value: Long)
