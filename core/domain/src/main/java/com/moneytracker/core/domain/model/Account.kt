package com.moneytracker.core.domain.model

data class Account(val id: AccountId, val name: String)

@JvmInline
value class AccountId(val value: Long)
