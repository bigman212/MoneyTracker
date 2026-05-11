package com.moneytracker.core.database.model

import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseAccount
import com.moneytracker.core.domain.model.ExpenseAccountId
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import java.time.Instant

internal fun ExpenseAccountEntity.toDomain(): ExpenseAccount =
    ExpenseAccount(
        id = ExpenseAccountId(id),
        name = name
    )

internal fun ExpenseCategoryEntity.toDomain(): ExpenseCategory =
    ExpenseCategory(
        id = ExpenseCategoryId(id),
        name = name,
        color = CategoryColor(colorArgb)
    )

internal fun ExpenseEntity.toDomain(): Expense =
    Expense(
        id = ExpenseId(id),
        name = name,
        categoryId = ExpenseCategoryId(categoryId),
        spentAt = Instant.ofEpochMilli(spentAtEpochMillis),
        accountId = ExpenseAccountId(accountId)
    )

internal fun Expense.toEntity(): ExpenseEntity =
    ExpenseEntity(
        id = id.value,
        name = name,
        categoryId = categoryId.value,
        spentAtEpochMillis = spentAt.toEpochMilli(),
        accountId = accountId.value
    )
