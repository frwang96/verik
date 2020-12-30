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

package verikc.tx

import verikc.base.config.ProjectConfig
import verikc.main.StatusPrinter
import verikc.sv.ast.SvCompilationUnit
import verikc.tx.ast.TxCompilationUnit
import verikc.tx.build.TxBuilderCompilationUnit

object TxStageDriver {

    fun build(compilationUnit: SvCompilationUnit, projectConfig: ProjectConfig): TxCompilationUnit {
        return TxBuilderCompilationUnit.build(compilationUnit, projectConfig)
    }

    fun write(compilationUnit: TxCompilationUnit, projectConfig: ProjectConfig) {
        StatusPrinter.info("writing output files")
        projectConfig.pathConfig.orderFile.parentFile.mkdirs()
        projectConfig.pathConfig.orderFile.writeText(compilationUnit.orderString)
        StatusPrinter.info("+ ${projectConfig.pathConfig.orderFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)

        for (pkg in compilationUnit.pkgs) {
            if (pkg.wrapperString != null) {
                pkg.config.pkgWrapperFile.parentFile.mkdirs()
                pkg.config.pkgWrapperFile.writeText(pkg.wrapperString)
                StatusPrinter.info("+ ${pkg.config.pkgWrapperFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
            }
            for (file in pkg.files) {
                if (file.moduleString != null) {
                    file.config.outModuleFile.parentFile.mkdirs()
                    file.config.outModuleFile.writeText(file.moduleString)
                    StatusPrinter.info("+ ${file.config.outModuleFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
                }
                if (file.pkgString != null) {
                    file.config.outPkgFile.parentFile.mkdirs()
                    file.config.outPkgFile.writeText(file.pkgString)
                    StatusPrinter.info("+ ${file.config.outPkgFile.relativeTo(projectConfig.pathConfig.projectDir)}", 1)
                }
            }
        }
    }
}