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

package io.verik.compiler.interpret

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ModulePortParentResolverStageTest : BaseTest() {

    @Test
    fun `module port resolve parent`() {
        val projectContext = driveTest(
            ModulePortParentResolverStage::class,
            """
                class MP : ModulePort()
                class MI : ModuleInterface() {
                    @Make
                    val mp = MP()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ModulePort(MP, [], [], MI)",
            projectContext.findDeclaration("MP")
        )
    }

    @Test
    fun `module port resolve parent illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                ModulePortParentResolverStage::class,
                """
                    class MP : ModulePort()
                    class M : Module() {
                        @Make
                        val mp = MP()
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Module port instantiation used out of context", message) }
    }
}
