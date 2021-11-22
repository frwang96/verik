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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object PackageFileSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val packageTextFiles = ArrayList<TextFile>()
        projectContext.project.basicPackages.forEach {
            val packageOutputFile = serialize(projectContext, it)
            if (packageOutputFile != null)
                packageTextFiles.add(packageOutputFile)
        }
        projectContext.outputContext.packageTextFiles = packageTextFiles
    }

    private fun serialize(projectContext: ProjectContext, basicPackage: EBasicPackage): TextFile? {
        if (basicPackage.files.all { it.isEmptySerialization() })
            return null

        val outputPath = basicPackage.getOutputPathNotNull().resolve("Pkg.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            null,
            outputPath,
            FileHeaderBuilder.HeaderStyle.SV
        )
        val packageName = basicPackage.name
        val indent = " ".repeat(projectContext.config.indentLength)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("package $packageName;")
        basicPackage.files.forEach { file ->
            file.declarations.forEach {
                if (it is ESvBasicClass) {
                    builder.appendLine()
                    builder.append(indent)
                    builder.appendLine("typedef class ${it.name};")
                }
            }
        }
        basicPackage.files.forEach {
            if (!it.isEmptySerialization()) {
                val pathString = Platform.getStringFromPath(
                    projectContext.config.buildDir.relativize(it.getOutputPathNotNull())
                )
                builder.appendLine()
                builder.appendLine("`include \"$pathString\"")
            }
        }
        builder.appendLine()
        builder.appendLine("endpackage : $packageName")

        return TextFile(outputPath, builder.toString())
    }
}
