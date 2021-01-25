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
import verikc.sv.ast.SvPkg
import verikc.tx.ast.TxPkg

object TxBuilderPkg {

    fun build(pkg: SvPkg, projectConfig: ProjectConfig): TxPkg {
        return TxPkg(
            pkg.config,
            pkg.files.map { TxBuilderFile.build(it, projectConfig) },
            buildWrapperString(pkg, projectConfig)
        )
    }

    private fun buildWrapperString(pkg: SvPkg, projectConfig: ProjectConfig): String? {
        val fileConfigs = pkg.files
            .filter { it.hasPkgDeclarations() }
            .map { it.config }
        if (fileConfigs.isEmpty()) return null

        val fileHeader = projectConfig.header(pkg.config.dir, pkg.config.pkgWrapperFile)
        val builder = TxSourceBuilder(projectConfig.compileConfig.labelLines, fileHeader)

        builder.appendln("`timescale 1ns / 1ns")
        builder.appendln()
        builder.appendln("package ${pkg.config.identifierSv};")
        indent(builder) {
            for (file in pkg.files) {
                file.clses.forEach {
                    builder.appendln()
                    builder.appendln("typedef class ${it.identifier};")
                }
            }
            fileConfigs.forEach {
                builder.appendln()
                builder.appendln("`include \"${it.outPkgFile.name}\"")
            }
        }
        builder.appendln()
        builder.appendln("endpackage")
        return builder.toString()
    }
}