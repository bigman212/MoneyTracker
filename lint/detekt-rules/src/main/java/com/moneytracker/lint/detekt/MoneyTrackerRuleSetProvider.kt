package com.moneytracker.lint.detekt

import com.moneytracker.lint.detekt.rules.DataClassMultilineProperties
import com.moneytracker.lint.detekt.rules.MapperFunctionPrefix
import com.moneytracker.lint.detekt.rules.TopLevelDeclarationOrder
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class MoneyTrackerRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "moneytracker-conventions"

    override fun instance(config: Config): RuleSet =
        RuleSet(
            id = ruleSetId,
            rules = listOf(
                DataClassMultilineProperties(config),
                MapperFunctionPrefix(config),
                TopLevelDeclarationOrder(config)
            )
        )
}
