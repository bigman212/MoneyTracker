package com.moneytracker.core.data.model

import com.moneytracker.core.database.model.AccountEntity
import com.moneytracker.core.database.model.ExpenseCategoryEntity
import com.moneytracker.core.database.model.ExpenseEntity
import com.moneytracker.core.domain.model.Account
import com.moneytracker.core.domain.model.AccountId
import com.moneytracker.core.domain.model.CategoryColor
import com.moneytracker.core.domain.model.Expense
import com.moneytracker.core.domain.model.ExpenseCategory
import com.moneytracker.core.domain.model.ExpenseCategoryId
import com.moneytracker.core.domain.model.ExpenseId
import java.time.Instant

fun AccountEntity.asDomain(): Account = Account(
    id = AccountId(id),
    name = name
)

fun ExpenseCategoryEntity.asDomain(): ExpenseCategory = ExpenseCategory(
    id = ExpenseCategoryId(id),
    name = name,
    color = CategoryColor(colorArgb)
)

fun ExpenseEntity.asDomain(): Expense = Expense(
    id = ExpenseId(id),
    name = name,
    categoryId = ExpenseCategoryId(categoryId),
    spentAt = Instant.ofEpochMilli(spentAtEpochMillis),
    accountId = AccountId(accountId)
)

fun Expense.asEntity(): ExpenseEntity = ExpenseEntity(
    id = id.value,
    name = name,
    categoryId = categoryId.value,
    spentAtEpochMillis = spentAt.toEpochMilli(),
    accountId = accountId.value
)
