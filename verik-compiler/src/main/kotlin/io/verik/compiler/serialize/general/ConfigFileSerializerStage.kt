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

package io.verik.compiler.serialize.general

import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object ConfigFileSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("config.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            null,
            outputPath,
            FileHeaderBuilder.HeaderStyle.TXT
        )

        val synthesisTopNames = ArrayList<String>()
        val simulationTopNames = ArrayList<String>()
        val topVisitor = object : TreeVisitor() {
            override fun visitModule(module: EModule) {
                super.visitModule(module)
                if (module.isSynthesisTop)
                    synthesisTopNames.add(module.name)
                if (module.isSimulationTop)
                    simulationTopNames.add(module.name)
            }
        }
        projectContext.project.accept(topVisitor)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("timescale: ${projectContext.config.timescale}")
        if (synthesisTopNames.isNotEmpty()) {
            builder.appendLine("synthesisTop:")
            synthesisTopNames.forEach {
                builder.appendLine("  - $it")
            }
        }
        if (simulationTopNames.isNotEmpty()) {
            builder.appendLine("simulationTop:")
            simulationTopNames.forEach {
                builder.appendLine("  - $it")
            }
        }
        projectContext.outputContext.configTextFile = TextFile(outputPath, builder.toString())
    }
}
