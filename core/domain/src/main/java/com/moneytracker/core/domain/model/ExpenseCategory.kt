package com.moneytracker.core.domain.model

data class ExpenseCategory(
    val id: ExpenseCategoryId,
    val name: String,
    val color: CategoryColor
)

@JvmInline
value class ExpenseCategoryId(val value: Long)

@JvmInline
value class CategoryColor(val argb: Long)
