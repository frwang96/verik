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

package io.verik.importer.main

import io.verik.importer.ast.element.ECompilationUnit
import io.verik.importer.common.TextFile
import java.nio.file.Path

class ProjectContext(
    val config: VerikImporterConfig
) {

    var inputFileContexts: List<InputFileContext> = listOf()
    var includedTextFiles: HashMap<Path, TextFile> = HashMap()
    val processedProjectStages = HashSet<ProjectStage>()
    lateinit var compilationUnit: ECompilationUnit
    val outputContext = OutputContext()
}
