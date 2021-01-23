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

package verikc.daemon

import verikc.base.config.ProjectConfig
import verikc.main.StatusPrinter
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

object Daemon {

    fun run(projectConfig: ProjectConfig) {
        StatusPrinter.info("starting daemon")

        val watchService = FileSystems.getDefault().newWatchService()
        projectConfig.pathConfig.srcDir.toPath().register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        )

        while (true) {
            val watchKey = watchService.take()
            if (watchKey != null) {
                for (event in watchKey.pollEvents()) {
                    val path = event.context()
                    if (path is Path) {
                        StatusPrinter.info("updating ${path.toAbsolutePath()}", 1)
                    }
                }
                watchKey.reset()
            }
        }
    }
}