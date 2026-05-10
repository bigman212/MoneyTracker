package com.moneytracker.lint.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

class MapperFunctionPrefix(config: Config) : Rule(config) {
    companion object {
        private val AS_MAPPER_NAME = Regex("""as[A-Z]\w*""")
    }

    override val issue: Issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Style,
            description = "Mapper extension functions must use the to* prefix.",
            debt = Debt.FIVE_MINS
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val name = function.name.orEmpty()
        if (function.receiverTypeReference == null || !name.matches(AS_MAPPER_NAME)) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(function),
                message = "Mapper function '$name' must use to* prefix."
            )
        )
    }

}
