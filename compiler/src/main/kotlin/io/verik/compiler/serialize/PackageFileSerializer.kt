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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object PackageFileSerializer {

    fun serialize(projectContext: ProjectContext): List<TextFile> {
        val packageMap = buildPackageMap(projectContext)
        return packageMap.values.map {
            buildPackageFile(projectContext, it)
        }
    }

    private fun buildPackageMap(projectContext: ProjectContext): HashMap<PackageDeclaration, ArrayList<EFile>> {
        val packageMap = HashMap<PackageDeclaration, ArrayList<EFile>>()
        projectContext.project.files().forEach {
            if (it.packageDeclaration !in packageMap) {
                packageMap[it.packageDeclaration] = ArrayList()
            }
            packageMap[it.packageDeclaration]!!.add(it)
        }
        return packageMap
    }

    private fun buildPackageFile(projectContext: ProjectContext, files: List<EFile>): TextFile {
        val inputPath = files[0].inputPath.parent
        val outputPath = files[0].getOutputPathNotNull().parent.resolve("Pkg.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            inputPath,
            outputPath,
            FileHeaderBuilder.HeaderStyle.SV
        )
        val packageName = files[0].packageDeclaration.name
        val indent = " ".repeat(projectContext.config.indentLength)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("package $packageName;")
        files.forEach { file ->
            file.members.forEach {
                if (it is ESvBasicClass) {
                    builder.appendLine()
                    builder.append(indent)
                    builder.appendLine("typedef class ${it.name};")
                }
            }
        }
        files.forEach {
            builder.appendLine()
            builder.appendLine("`include \"${it.getOutputPathNotNull().fileName}\"")
        }
        builder.appendLine()
        builder.appendLine("endpackage : $packageName")

        return TextFile(outputPath, builder.toString())
    }
}