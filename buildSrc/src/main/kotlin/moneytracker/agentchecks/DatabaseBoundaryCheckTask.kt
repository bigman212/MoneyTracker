package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: keeps Room implementation details inside :core:database and
 * prevents generated database ids from drifting back into Room annotations.
 */
abstract class DatabaseBoundaryCheckTask : DefaultTask() {
    @get:Internal
    abstract val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks Room DAO/entity boundaries and id/upsert conventions."
    }

    @TaskAction
    fun checkDatabaseBoundary() {
        val root = projectRoot.get().asFile
        val violations = mutableListOf<String>()
        val databaseSource = root.resolve("core/database/src/main/java")
        val daoSource = databaseSource.resolve("com/moneytracker/core/database/dao")
        val modelSource = databaseSource.resolve("com/moneytracker/core/database/model")

        fun report(filePath: String, message: String) {
            violations += "$filePath: $message"
        }

        listOf(daoSource, modelSource)
            .filter { it.exists() }
            .flatMap { sourceDir ->
                sourceDir.walkTopDown()
                    .filter { file -> file.isFile && file.extension == "kt" }
                    .toList()
            }
            .forEach { file ->
                val relativePath = file.relativeTo(root).invariantSeparatorsPath
                val text = file.readText()
                val isDao = relativePath.contains("/dao/")
                val hasRoomEntity = text.contains("@Entity(") || text.contains("@Entity\n")

                if (isDao && !Regex("""internal\s+interface\s+\w+Dao\b""").containsMatchIn(text)) {
                    report(relativePath, "DAO interfaces must be internal.")
                }
                if (hasRoomEntity && !Regex("""internal\s+data\s+class\s+\w+Entity\b""").containsMatchIn(text)) {
                    report(relativePath, "Room entities must be internal.")
                }
            }

        root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "kt" &&
                    !file.hasIgnoredDirectorySegment()
            }
            .forEach { file ->
                val relativePath = file.relativeTo(root).invariantSeparatorsPath
                val text = file.readText()
                val inDatabaseModule = relativePath.startsWith("core/database/")

                if (!inDatabaseModule) {
                    if (text.contains("com.moneytracker.core.database.dao.")) {
                        report(relativePath, "DAO types must not be imported outside :core:database.")
                    }
                    if (text.contains("com.moneytracker.core.database.model.")) {
                        report(relativePath, "Room entity types must not be imported outside :core:database.")
                    }
                }

                if (text.contains("autoGenerate = true")) {
                    report(relativePath, "Do not use Room autoGenerate ids; generate ids in repository code.")
                }
                if (text.contains("OnConflictStrategy.ABORT")) {
                    report(relativePath, "Prefer @Upsert over OnConflictStrategy.ABORT.")
                }
                if (Regex("""@Insert\b""").containsMatchIn(text)) {
                    report(relativePath, "Prefer @Upsert over @Insert for DAO writes.")
                }
            }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("databaseBoundaryCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }
}
