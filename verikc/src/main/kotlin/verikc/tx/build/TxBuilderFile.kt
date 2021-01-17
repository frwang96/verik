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

import verikc.base.config.ProjectConfig
import verikc.sv.ast.SvFile
import verikc.tx.ast.TxFile

object TxBuilderFile {

    fun build(file: SvFile, projectConfig: ProjectConfig): TxFile {
        return TxFile(
            file.config,
            buildComponentString(file, projectConfig),
            buildPkgString(file, projectConfig)
        )
    }

    private fun buildComponentString(file: SvFile, projectConfig: ProjectConfig): String? {
        if (!file.hasComponentDeclarations()) return null
        val fileHeader = projectConfig.header(file.config.file, file.config.outComponentFile)
        val builder = TxSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)

        file.modules.forEach {
            TxBuilderModule.build(it, builder)
            builder.appendln()
        }

        file.busses.forEach {
            TxBuilderBus.build(it, builder)
            builder.appendln()
        }

        return builder.toString()
    }

    private fun buildPkgString(file: SvFile, projectConfig: ProjectConfig): String? {
        if (!file.hasPkgDeclarations()) return null
        val fileHeader = projectConfig.header(file.config.file, file.config.outPkgFile)
        val builder = TxSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)

        file.primaryProperties.forEach {
            TxBuilderPrimaryProperty.build(it, builder)
            builder.appendln()
        }
        file.enums.forEach {
            TxBuilderEnum.build(it, builder)
            builder.appendln()
        }
        file.clses.forEach {
            TxBuilderCls.build(it, builder)
            builder.appendln()
        }

        return builder.toString()
    }
}