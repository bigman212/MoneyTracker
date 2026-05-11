package moneytracker.agentchecks

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Registers the single root harness task: `agentCheck`.
 *
 * The task intentionally depends on matching verification tasks across all modules in
 * this order: architecture checks, project convention checks, formatting, Kotlin style,
 * static analysis, Android lint, unit tests, and debug assembly.
 */
class MoneyTrackerAgentChecksPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val agentCheckTaskNames = listOf(
            "agentErrorsCheck",
            "agentsDuplicateLinesCheck",
            "agentArchitectureCheck",
            "companionObjectTopCheck",
            "databaseBoundaryCheck",
            "composeStabilityCheck",
            "spotlessCheck",
            "ktlintCheck",
            "detekt",
            "konsistCheck",
            "lintDebug",
            "testDebugUnitTest",
            "assembleDebug",
        )

        val agentErrorsCheck =
            target.registerProjectRootCheck<AgentErrorsCheckTask>("agentErrorsCheck")
        val agentsDuplicateLinesCheck =
            target.registerProjectRootCheck<AgentsDuplicateLinesCheckTask>("agentsDuplicateLinesCheck")
        val agentArchitectureCheck =
            target.registerProjectRootCheck<AgentArchitectureCheckTask>("agentArchitectureCheck")
        val companionObjectTopCheck =
            target.registerProjectRootCheck<CompanionObjectTopCheckTask>("companionObjectTopCheck")
        val databaseBoundaryCheck =
            target.registerProjectRootCheck<DatabaseBoundaryCheckTask>("databaseBoundaryCheck")
        val composeStabilityCheck =
            target.registerProjectRootCheck<ComposeStabilityCheckTask>("composeStabilityCheck")
        val konsistCheck = target.tasks.register("konsistCheck") {
            group = "verification"
            description = "Runs Konsist architecture and declaration tests."
        }

        val agentCheck = target.tasks.register("agentCheck") {
            group = "verification"
            description = "Runs the full project harness check."
            dependsOn(
                agentErrorsCheck,
                agentsDuplicateLinesCheck,
                agentArchitectureCheck,
                companionObjectTopCheck,
                databaseBoundaryCheck,
                composeStabilityCheck,
                konsistCheck
            )
        }

        target.gradle.projectsEvaluated {
            val orderedTasks = agentCheckTaskNames.flatMap { taskName ->
                val matchingTasks = target.allprojects.mapNotNull { project ->
                    project.tasks.findByName(taskName)
                }

                if (matchingTasks.isEmpty()) {
                    target.logger.warn("agentCheck: task '$taskName' was not found; skipping it.")
                }

                matchingTasks
            }

            agentCheck.configure {
                dependsOn(orderedTasks)
            }

            composeStabilityCheck.configure {
                dependsOn(
                    target.allprojects.mapNotNull { project ->
                        project.tasks.findByName("compileDebugKotlin")
                    }
                )
            }

            konsistCheck.configure {
                dependsOn(target.project(":lint:detekt-rules").tasks.named("test"))
            }

            orderedTasks.zipWithNext { previousTask, nextTask ->
                nextTask.mustRunAfter(previousTask)
            }
        }
    }
}

private inline fun <reified T> Project.registerProjectRootCheck(name: String): TaskProvider<T>
    where T : Task, T : ProjectRootCheckTask =
    tasks.register(name, T::class.java) {
        projectRoot.set(layout.projectDirectory)
    }
