/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.message.WarningMessageTemplate0
import io.verik.compiler.message.WarningMessageTemplate1
import kotlin.reflect.full.declaredMemberProperties

object ConfigCheckerStage : ProjectStage() {

    private val timescaleRegex = Regex(
        "\\s*(1|10|100)\\s*(s|ms|us|ns|ps|fs)\\s*/\\s*(1|10|100)\\s*(s|ms|us|ns|ps|fs)\\s*"
    )

    override fun process(projectContext: ProjectContext) {
        val timescale = projectContext.config.timescale
        if (!timescale.matches(timescaleRegex))
            Messages.INVALID_TIMESCALE.on(SourceLocation.NULL, timescale)

        val warnings = getWarnings()
        projectContext.config.suppressedWarnings.forEach {
            if (it !in warnings)
                Messages.UNRECOGNIZED_WARNING.on(SourceLocation.NULL, it)
        }
        projectContext.config.promotedWarnings.forEach {
            if (it !in warnings)
                Messages.UNRECOGNIZED_WARNING.on(SourceLocation.NULL, it)
        }
    }

    private fun getWarnings(): List<String> {
        return Messages::class.declaredMemberProperties.mapNotNull {
            when (val messageTemplate = it.get(Messages)) {
                is WarningMessageTemplate0 -> messageTemplate.name
                is WarningMessageTemplate1<*> -> messageTemplate.name
                else -> null
            }
        }
    }
}
