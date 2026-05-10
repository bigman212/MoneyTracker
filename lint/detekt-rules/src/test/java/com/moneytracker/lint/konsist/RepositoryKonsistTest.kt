package com.moneytracker.lint.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class RepositoryKonsistTest {
    companion object {
        private val forbiddenPersistenceNames = listOf(
            "Database",
            "Room",
            "Sql",
            "Dao",
        )
    }

    @Test
    fun `repository implementations live in data repository package`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { declaration ->
                declaration.resideInPackage("..core.data.repository..")
            }
    }

    @Test
    fun `data repository implementations do not expose persistence technology in name`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .assertFalse { declaration ->
                declaration.resideInPackage("..core.data.repository..") &&
                    declaration.name.endsWith("Repository") &&
                    forbiddenPersistenceNames.any { namePart ->
                        declaration.name.contains(namePart)
                    }
            }
    }

    @Test
    fun `data repository classes use impl suffix`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .assertTrue { declaration ->
                !declaration.resideInPackage("..core.data.repository..") ||
                    !declaration.name.endsWith("Repository") ||
                    declaration.name.endsWith("RepositoryImpl")
            }
    }

}
