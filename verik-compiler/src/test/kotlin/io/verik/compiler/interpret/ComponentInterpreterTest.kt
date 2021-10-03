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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ComponentInterpreterTest : BaseTest() {

    @Test
    fun `interpret module simple`() {
        val projectContext = driveTest(
            ClassInterpreterStage::class,
            """
                class M: Module()
            """.trimIndent()
        )
        assertElementEquals(
            "Module(M, [], [], [])",
            projectContext.findDeclaration("M")
        )
    }

    @Test
    fun `interpret module with port`() {
        val projectContext = driveTest(
            ClassInterpreterStage::class,
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent()
        )
        assertElementEquals(
            "Module(M, [], [], [Port(x, Boolean, INPUT)])",
            projectContext.findDeclaration("M")
        )
    }

    @Test
    fun `interpret module with port illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                ClassInterpreterStage::class,
                """
                    class M(var x: Boolean): Module()
                """.trimIndent()
            )
        }.apply { Assertions.assertEquals("Could not determine directionality of port: x", message) }
    }

    @Test
    fun `interpret module interface`() {
        val projectContext = driveTest(
            ClassInterpreterStage::class,
            """
                class I: Interface()
            """.trimIndent()
        )
        assertElementEquals(
            "ModuleInterface(I, [], [], [])",
            projectContext.findDeclaration("I")
        )
    }

    @Test
    fun `interpret module port`() {
        val projectContext = driveTest(
            ClassInterpreterStage::class,
            """
                class MP: Modport()
            """.trimIndent()
        )
        assertElementEquals(
            "ModulePort(MP, [], [])",
            projectContext.findDeclaration("MP")
        )
    }

    @Test
    fun `interpret clocking block`() {
        val projectContext = driveTest(
            ClassInterpreterStage::class,
            """
                class CB(override val event: Event): ClockingBlock()
            """.trimIndent()
        )
        assertElementEquals(
            "ClockingBlock(CB, [], [], 0)",
            projectContext.findDeclaration("CB")
        )
    }
}
