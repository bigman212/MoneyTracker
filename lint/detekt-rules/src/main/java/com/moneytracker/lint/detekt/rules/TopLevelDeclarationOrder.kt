package com.moneytracker.lint.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

class TopLevelDeclarationOrder(config: Config) : Rule(config) {
    override val issue: Issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Style,
            description = "Top-level types in one file must be declared before use.",
            debt = Debt.FIVE_MINS
        )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val declarations = file.declarations
            .filterIsInstance<KtClassOrObject>()
            .mapNotNull { declaration ->
                val name = declaration.name ?: return@mapNotNull null
                TopLevelDeclaration(
                    name = name,
                    declaration = declaration
                )
            }

        declarations.forEachIndexed { index, declaration ->
            val laterDeclarations = declarations.drop(index + 1)
            laterDeclarations
                .filter { laterDeclaration -> declaration.referencesType(laterDeclaration.name) }
                .forEach { laterDeclaration ->
                    report(
                        CodeSmell(
                            issue = issue,
                            entity = Entity.from(declaration.declaration),
                            message = "${laterDeclaration.name} is used before its top-level declaration."
                        )
                    )
                }
        }
    }

    private fun TopLevelDeclaration.referencesType(typeName: String): Boolean =
        Regex("""\b${Regex.escape(typeName)}\b""").containsMatchIn(declaration.text)

    private data class TopLevelDeclaration(
        val name: String,
        val declaration: KtClassOrObject,
    )
}
