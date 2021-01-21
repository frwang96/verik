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
import verikc.sv.ast.SvCompilationUnit
import verikc.tx.ast.TxCompilationUnit
import java.io.File

object TxBuilderCompilationUnit {

    fun build(compilationUnit: SvCompilationUnit, projectConfig: ProjectConfig): TxCompilationUnit {
        return TxCompilationUnit(
            compilationUnit.pkgs.map { TxBuilderPkg.build(it, projectConfig) },
            buildOrderString(compilationUnit, projectConfig)
        )
    }

    private fun buildOrderString(compilationUnit: SvCompilationUnit, projectConfig: ProjectConfig): String {
        val files = ArrayList<File>()
        compilationUnit.pkgs.forEach {
            if (it.files.any { file -> file.hasPkgDeclarations() }) {
                files.add(it.config.pkgWrapperFile)
            }
            it.files.forEach { file ->
                if (file.hasComponentDeclarations()) {
                    files.add(file.config.outComponentFile)
                }
            }
        }

        val builder = StringBuilder()
        builder.appendLine(projectConfig.compileConfig.top)
        files.forEach {
            builder.appendLine(it.relativeTo(projectConfig.pathConfig.outDir))
        }
        return builder.toString()
    }
}