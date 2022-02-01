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

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object PackageWrapperSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val packageWrapperTextFiles = ArrayList<TextFile>()
        projectContext.project.nativeRegularPackages.forEach {
            val packageWrapperTextFile = serialize(projectContext, it)
            if (packageWrapperTextFile != null)
                packageWrapperTextFiles.add(packageWrapperTextFile)
        }
        projectContext.outputContext.packageWrapperTextFiles = packageWrapperTextFiles
    }

    private fun serialize(projectContext: ProjectContext, `package`: EPackage): TextFile? {
        if (`package`.files.all { it.isEmptySerialization() })
            return null

        val outputPath = `package`.outputPath.resolve("Pkg.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            outputPath,
            FileHeaderBuilder.HeaderStyle.SYSTEM_VERILOG
        )
        val packageName = `package`.name
        val indent = " ".repeat(projectContext.config.indentLength)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("package $packageName;")
        `package`.files.forEach { file ->
            file.declarations.forEach {
                if (it is ESvClass) {
                    builder.appendLine()
                    builder.append(indent)
                    builder.appendLine("typedef class ${it.name};")
                }
            }
        }
        `package`.files.forEach {
            if (!it.isEmptySerialization()) {
                val pathString = Platform.getStringFromPath(
                    projectContext.config.buildDir.relativize(it.outputPath)
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
