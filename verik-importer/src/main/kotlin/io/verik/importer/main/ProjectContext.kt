/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import io.verik.importer.ast.element.common.EProject
import io.verik.importer.common.TextFile
import java.nio.file.Path

/**
 * Context that stores all of the internal state of the importer.
 */
class ProjectContext(
    val config: VerikImporterConfig
) {

    val report = VerikImporterReport()
    var inputFileContexts: List<InputFileContext> = listOf()
    var includedTextFiles: HashMap<Path, TextFile> = HashMap()
    val processedProjectStages = HashSet<ProjectStage>()
    var project: EProject = EProject(arrayListOf())
    val outputContext = OutputContext()
}
