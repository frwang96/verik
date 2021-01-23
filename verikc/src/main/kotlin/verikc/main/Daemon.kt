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

package verikc.main

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import verikc.al.AlFileParser
import verikc.al.ast.AlCompilationUnit
import verikc.al.ast.AlFile
import verikc.al.ast.AlPkg
import verikc.base.config.ProjectConfig
import verikc.kt.KtStageDriver
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey

object Daemon {

    var trigger: String? = null

    fun run(projectConfig: ProjectConfig) {
        StatusPrinter.info("starting daemon")

        val watchService = FileSystems.getDefault().newWatchService()
        val watchKeyMap = HashMap<WatchKey, File>()
        for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
            val watchKey = pkgConfig.dir.toPath().register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            )
            watchKeyMap[watchKey] = pkgConfig.dir
        }

        GlobalScope.launch {
            while (true) {
                if (trigger != null) {
                    StatusPrinter.info("refresh triggered by $trigger", 1)
                    trigger = null
                    refresh(projectConfig.pathConfig.configFile.path)
                    delay(1000)
                }
                delay(10)
            }
        }

        trigger = projectConfig.pathConfig.configFile.relativeTo(projectConfig.pathConfig.projectDir).path
        while (true) {
            val watchKey = watchService.take()
            if (watchKey != null) {
                for (event in watchKey.pollEvents()) {
                    val name = event.context().toString()
                    if (name != "Headers.kt") {
                        val file = watchKeyMap[watchKey]!!.resolve(name)
                        trigger = file.relativeTo(projectConfig.pathConfig.projectDir).path
                    }
                }
                watchKey.reset()
            }
        }
    }

    private fun refresh(configPath: String) {
        try {
            val projectConfig = ProjectConfig(configPath)
            val pkgs = ArrayList<AlPkg>()
            for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
                val files = ArrayList<AlFile>()
                pkgConfig.fileConfigs.forEach { files.add(AlFileParser.parse(it)) }
                pkgs.add(AlPkg(pkgConfig, files))
            }
            val compilationUnit = KtStageDriver.parse(
                AlCompilationUnit(pkgs),
                projectConfig.compileConfig.topIdentifier,
                projectConfig.symbolContext
            )
            HeaderBuilder.build(projectConfig, compilationUnit)
        } catch (e: Exception) {
            if (e.message != null) {
                StatusPrinter.errorMessage(e.message!!)
                println()
            }
        }
    }
}