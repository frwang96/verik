/*
 * Copyright 2020 Francis Wang
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

package verikc.al

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import verikc.al.ast.AlCompilationUnit
import verikc.al.ast.AlFile
import verikc.al.ast.AlPkg
import verikc.base.config.FileConfig
import verikc.base.config.ProjectConfig
import verikc.base.symbol.Symbol
import verikc.main.HashBuilder
import verikc.main.StatusPrinter
import java.io.File

object AlDriver {

    fun drive(projectConfig: ProjectConfig): AlCompilationUnit {
        StatusPrinter.info("parsing input files", 1)

        val deferredFiles = HashMap<Symbol, Deferred<AlFile>>()
        for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
            for (fileConfig in pkgConfig.fileConfigs) {
                deferredFiles[fileConfig.symbol] = GlobalScope.async {
                    parseFile(fileConfig, projectConfig)
                }
            }
        }

        val pkgs = ArrayList<AlPkg>()
        for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
            val files = ArrayList<AlFile>()
            for (fileConfig in pkgConfig.fileConfigs) {
                runBlocking {
                    files.add(deferredFiles[fileConfig.symbol]!!.await())
                }
            }
            pkgs.add(AlPkg(pkgConfig, files))
        }

        val compilationUnit = AlCompilationUnit(pkgs)
        updateCache(compilationUnit, projectConfig)
        return compilationUnit
    }

    private fun parseFile(fileConfig: FileConfig, projectConfig: ProjectConfig): AlFile {
        val txtFile = fileConfig.copyFile.readText()
        val hash = HashBuilder.build(txtFile)

        var alFile: AlTree? = null
        if (hash == fileConfig.hash && fileConfig.cacheFile.exists()) {
            alFile = AlTreeSerializer.deserialize(fileConfig.symbol, fileConfig.cacheFile.readBytes())
        }
        val isCached = (alFile != null)
        alFile = alFile ?: AlTreeParser.parseKotlinFile(fileConfig.symbol, txtFile)

        StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
        return AlFile(fileConfig, hash, isCached, alFile)
    }

    private fun updateCache(compilationUnit: AlCompilationUnit, projectConfig: ProjectConfig) {
        deleteCacheFiles(compilationUnit, projectConfig)
        writeCacheFiles(compilationUnit)

        val builder = StringBuilder()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                builder.appendLine("${file.config.identifier} ${file.hash}")
            }
        }
        projectConfig.pathConfig.hashFile.writeText(builder.toString())
    }

    private fun deleteCacheFiles(compilationUnit: AlCompilationUnit, projectConfig: ProjectConfig) {
        val fileSet = HashSet<File>()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                fileSet.add(file.config.cacheFile)
            }
        }

        val fileList = ArrayList<File>()
        projectConfig.pathConfig.cacheDir.walk().forEach {
            if (it.isFile && it !in fileSet) fileList.add(it)
        }
        fileList.forEach { it.delete() }
    }

    private fun writeCacheFiles(compilationUnit: AlCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                if (!file.isCached) {
                    file.config.cacheFile.parentFile.mkdirs()
                    file.config.cacheFile.writeBytes(AlTreeSerializer.serialize(file.tree))
                }
            }
        }
    }
}