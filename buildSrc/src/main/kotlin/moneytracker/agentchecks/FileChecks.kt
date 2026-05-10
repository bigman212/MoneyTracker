package moneytracker.agentchecks

import java.io.File
import org.gradle.api.file.DirectoryProperty

internal interface ProjectRootCheckTask {
    val projectRoot: DirectoryProperty
}

internal fun File.hasIgnoredDirectorySegment(): Boolean {
    val pathSegments = invariantSeparatorsPath.split('/')
    return "build" in pathSegments || "buildSrc" in pathSegments || ".gradle" in pathSegments
}
