/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

/**
 * Check that the compiler configuration is valid.
 */
object ConfigCheckerStage : ProjectStage() {

    private val timescaleRegex = Regex(
        "\\s*(1|10|100)\\s*(s|ms|us|ns|ps|fs)\\s*/\\s*(1|10|100)\\s*(s|ms|us|ns|ps|fs)\\s*"
    )

    override fun process(projectContext: ProjectContext) {
        val timescale = projectContext.config.timescale
        if (!timescale.matches(timescaleRegex)) {
            Messages.INVALID_TIMESCALE.on(SourceLocation.NULL, timescale)
        }
    }
}
