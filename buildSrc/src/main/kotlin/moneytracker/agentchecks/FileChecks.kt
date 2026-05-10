package moneytracker.agentchecks

import java.io.File

internal fun File.hasIgnoredDirectorySegment(): Boolean {
    val pathSegments = invariantSeparatorsPath.split('/')
    return "build" in pathSegments || "buildSrc" in pathSegments || ".gradle" in pathSegments
}
