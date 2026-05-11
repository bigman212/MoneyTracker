package com.moneytracker.lint.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

class DataClassMultilineProperties(config: Config) : Rule(config) {
    companion object {
        private const val MULTILINE_PARAMETER_COUNT = 2
    }

    override val issue: Issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Style,
            description = "Data classes with multiple properties must place each property on its own line.",
            debt = Debt.FIVE_MINS
        )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (!klass.isData()) return

        val valueParameters = klass.primaryConstructorParameters.filter { parameter ->
            parameter.hasValOrVar()
        }
        if (valueParameters.size < MULTILINE_PARAMETER_COUNT) return

        val constructorText = klass.primaryConstructor?.text.orEmpty()
        if (constructorText.contains('\n')) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(klass),
                message = "Data class '${klass.name}' must put constructor properties on separate lines."
            )
        )
    }

}
