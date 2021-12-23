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
    fun `module simple`() {
        driveElementTest(
            """
                class M: Module()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "Module(M, M, [], [])"
        ) { it.findDeclaration("M") }
    }

    @Test
    fun `module simulation top illegal`() {
        driveMessageTest(
            """
                @SimTop
                class M: Module()
            """.trimIndent(),
            true,
            "Simulation top must be declared as object"
        )
    }

    @Test
    fun `module synthesis top illegal`() {
        driveMessageTest(
            """
                @SynthTop
                object M: Module()
            """.trimIndent(),
            true,
            "Synthesis top must not be declared as object"
        )
    }

    @Test
    fun `module with port`() {
        driveElementTest(
            """
                class M(@In var x: Boolean): Module()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "Module(M, M, [], [Port(x, Boolean, INPUT)])"
        ) { it.findDeclaration("M") }
    }

    @Test
    fun `module with port no directionality`() {
        driveMessageTest(
            """
                class M(var x: Boolean): Module()
            """.trimIndent(),
            true,
            "Could not determine directionality of port: x"
        )
    }

    @Test
    fun `module with port immutable`() {
        driveMessageTest(
            """
                class M(@In val x: Boolean): Module()
            """.trimIndent(),
            true,
            "Port must be declared as var: x"
        )
    }

    @Test
    fun `module interface`() {
        driveElementTest(
            """
                class MI: ModuleInterface()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ModuleInterface(MI, MI, [], [])"
        ) { it.findDeclaration("MI") }
    }

    @Test
    fun `module port`() {
        driveElementTest(
            """
                class MP: ModulePort()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ModulePort(MP, MP, [], null)"
        ) { it.findDeclaration("MP") }
    }

    @Test
    fun `clocking block`() {
        driveElementTest(
            """
                class CB(override val event: Event): ClockingBlock()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "ClockingBlock(CB, CB, [], 0)"
        ) { it.findDeclaration("CB") }
    }
}
