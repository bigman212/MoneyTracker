package com.moneytracker.lint.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

class ArchitectureKonsistTest {
    @Test
    fun `core layers have expected dependencies`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("Domain", "com.moneytracker.core.domain..")
                val data = Layer("Data", "com.moneytracker.core.data..")
                val database = Layer("Database", "com.moneytracker.core.database..")

                domain.dependsOnNothing()
                data.dependsOn(domain)
                data.dependsOn(database)
                database.dependsOn(domain)
            }
    }
}
