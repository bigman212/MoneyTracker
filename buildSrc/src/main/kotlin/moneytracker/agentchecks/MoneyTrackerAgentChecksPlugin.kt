package moneytracker.agentchecks

import org.gradle.api.Plugin
import org.gradle.api.Project

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
            "agentArchitectureCheck",
            "companionObjectTopCheck",
            "composeStabilityCheck",
            "spotlessCheck",
            "ktlintCheck",
            "detekt",
            "lintDebug",
            "testDebugUnitTest",
            "assembleDebug",
        )

        val companionObjectTopCheck = target.tasks.register(
            "companionObjectTopCheck",
            CompanionObjectTopCheckTask::class.java
        ) {
            projectRoot.set(target.layout.projectDirectory)
        }

        val agentArchitectureCheck = target.tasks.register(
            "agentArchitectureCheck",
            AgentArchitectureCheckTask::class.java
        ) {
            projectRoot.set(target.layout.projectDirectory)
        }

        val agentErrorsCheck = target.tasks.register(
            "agentErrorsCheck",
            AgentErrorsCheckTask::class.java
        ) {
            projectRoot.set(target.layout.projectDirectory)
        }

        val composeStabilityCheck = target.tasks.register(
            "composeStabilityCheck",
            ComposeStabilityCheckTask::class.java
        ) {
            projectRoot.set(target.layout.projectDirectory)
        }

        val agentCheck = target.tasks.register("agentCheck") {
            group = "verification"
            description = "Runs the full project harness check."
            dependsOn(agentErrorsCheck, agentArchitectureCheck, companionObjectTopCheck, composeStabilityCheck)
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

            orderedTasks.zipWithNext { previousTask, nextTask ->
                nextTask.mustRunAfter(previousTask)
            }
        }
    }
}
