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

object FileAnnotationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach { file ->
            val isAnnotated = file.annotationEntries.any { it.shortName.toString() == "Verik" }
            if (!isAnnotated) {
                Messages.FILE_NOT_ANNOTATED.on(file)
            }
        }
    }
}
