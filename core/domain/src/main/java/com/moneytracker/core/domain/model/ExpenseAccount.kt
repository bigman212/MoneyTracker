package com.moneytracker.core.domain.model

@JvmInline
value class ExpenseAccountId(val value: Long)

data class ExpenseAccount(
    val id: ExpenseAccountId,
    val name: String
)
