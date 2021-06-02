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

import io.verik.compiler.ast.common.PackageName
import io.verik.compiler.ast.common.SourceType
import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkOutputFile
import io.verik.compiler.common.ElementUtil
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object PackageFileSerializer {

    fun serialize(projectContext: ProjectContext): List<TextFile> {
        val packageMap = buildPackageMap(projectContext)
        return packageMap.values.map {
            buildPackageFile(projectContext, it)
        }
    }

    private fun buildPackageMap(projectContext: ProjectContext): HashMap<PackageName, ArrayList<VkOutputFile>> {
        val packageMap = HashMap<PackageName, ArrayList<VkOutputFile>>()
        projectContext.vkFiles.forEach {
            val file = ElementUtil.cast<VkOutputFile>(it)
            if (file != null && file.sourceType == SourceType.PACKAGE) {
                if (file.packageName !in packageMap) {
                    packageMap[file.packageName] = ArrayList()
                }
                packageMap[file.packageName]!!.add(file)
            }
        }
        return packageMap
    }

    private fun buildPackageFile(projectContext: ProjectContext, files: List<VkOutputFile>): TextFile {
        val inputPath = files[0].inputPath.parent
        val outputPath = files[0].outputPath.parent.resolve("Pkg.sv")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            inputPath,
            outputPath,
            FileHeaderBuilder.HeaderStyle.SYSTEM_VERILOG_DECORATED
        )
        val serializedPackageName = files[0].packageName.serialize()
        val indent = " ".repeat(projectContext.config.indentLength)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("package $serializedPackageName;")
        files.forEach { file ->
            file.declarations.forEach {
                if (it is VkBaseClass) {
                    builder.appendLine()
                    builder.append(indent)
                    builder.appendLine("typedef class ${it.name};")
                }
            }
        }
        files.forEach {
            builder.appendLine()
            builder.appendLine("`include \"${it.outputPath.fileName}\"")
        }
        builder.appendLine()
        builder.appendLine("endpackage: $serializedPackageName")

        return TextFile(outputPath, builder.toString())
    }
}