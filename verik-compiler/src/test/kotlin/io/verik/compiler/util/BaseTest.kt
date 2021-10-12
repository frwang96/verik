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

package io.verik.compiler.util

import io.verik.compiler.main.Config
import io.verik.compiler.main.Platform
import io.verik.compiler.message.MessageCollector
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Paths

abstract class BaseTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            MessageCollector.messageCollector = MessageCollector(getConfig(), TestMessagePrinter())
        }

        fun getConfig(): Config {
            val projectDir = if (Platform.isWindows) "C:\\" else "/"
            val buildDir = if (Platform.isWindows) "C:\\build\\verik" else "/build/verik"
            val projectFile = if (Platform.isWindows) {
                "C:\\src\\main\\kotlin\\verik\\Test.kt"
            } else {
                "/src/main/kotlin/verik/Test.kt"
            }
            return Config(
                version = "local-SNAPSHOT",
                timestamp = "",
                projectName = "verik",
                projectDir = Paths.get(projectDir),
                buildDir = Paths.get(buildDir),
                projectFiles = listOf(Paths.get(projectFile)),
                debug = true,
                suppressedWarnings = listOf("KOTLIN_COMPILE_WARNING"),
                promotedWarnings = listOf(),
                maxErrorCount = 0,
                labelLines = false,
                wrapLength = 80,
                indentLength = 4,
                enableDeadCodeElimination = false
            )
        }
    }
}
