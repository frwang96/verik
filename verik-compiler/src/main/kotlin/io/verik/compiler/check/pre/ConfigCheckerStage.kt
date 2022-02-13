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
