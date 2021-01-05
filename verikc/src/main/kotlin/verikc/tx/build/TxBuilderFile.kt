/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.tx.build

import verikc.base.ast.LineException
import verikc.base.config.ProjectConfig
import verikc.sv.ast.SvDeclaration
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvFile
import verikc.sv.ast.SvModule
import verikc.tx.ast.TxFile
import java.io.File

object TxBuilderFile {

    fun build(file: SvFile, projectConfig: ProjectConfig): TxFile {
        return TxFile(
            file.config,
            buildString(file.componentDeclarations, file.config.file, file.config.outComponentFile, projectConfig),
            buildString(file.pkgDeclarations, file.config.file, file.config.outPkgFile, projectConfig)
        )
    }

    private fun buildString(
        declarations: List<SvDeclaration>,
        inFile: File,
        outFile: File,
        projectConfig: ProjectConfig
    ): String? {
        if (declarations.isEmpty()) return null
        val fileHeader = projectConfig.header(inFile, outFile)
        val builder = TxSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)
        declarations.forEach {
            buildDeclaration(it, builder)
        }
        return builder.toString()
    }

    private fun buildDeclaration(declaration: SvDeclaration, builder: TxSourceBuilder) {
        when (declaration) {
            is SvModule -> TxBuilderModule.build(declaration, builder)
            is SvEnum -> TxBuilderEnum.build(declaration, builder)
            else -> throw LineException("top level declaration not supported", declaration.line)
        }
    }
}