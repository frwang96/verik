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

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ComponentInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret module simple`() {
        driveTest(
            """
                class M: Module()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "Module(M, M, [], [])"
        ) { it.findDeclaration("M") }
    }

    @Test
    fun `interpret module with port`() {
        driveTest(
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "Module(M, M, [], [Port(x, Boolean, INPUT)])"
        ) { it.findDeclaration("M") }
    }

    @Test
    fun `interpret module with port no directionality`() {
        driveTest(
            """
                class M(var x: Boolean): Module()
            """.trimIndent(),
            true,
            "Could not determine directionality of port: x"
        )
    }

    @Test
    fun `interpret module with port immutable`() {
        driveTest(
            """
                class M(@In val x: Boolean): Module()
            """.trimIndent(),
            true,
            "Port must be declared as var: x"
        )
    }

    @Test
    fun `interpret module interface`() {
        driveTest(
            """
                class MI: ModuleInterface()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ModuleInterface(MI, MI, [], [])"
        ) { it.findDeclaration("MI") }
    }

    @Test
    fun `interpret module port`() {
        driveTest(
            """
                class MP: ModulePort()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ModulePort(MP, MP, [], null)"
        ) { it.findDeclaration("MP") }
    }

    @Test
    fun `interpret clocking block`() {
        driveTest(
            """
                class CB(override val event: Event): ClockingBlock()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ClockingBlock(CB, CB, [], 0)"
        ) { it.findDeclaration("CB") }
    }
}
