/*
 * SPDX-License-Identifier: Apache-2.0
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
    fun `module with ports`() {
        driveElementTest(
            """
                class M(
                    @In val x: Boolean,
                    @In var y: Boolean,
                    @Out var z: Boolean,
                ): Module()
            """.trimIndent(),
            ComponentInterpreterStage::class,
            "Module(M, M, [], [Port(x, Boolean, INPUT), Port(y, Boolean, INPUT), Port(z, Boolean, OUTPUT)])"
        ) { it.findDeclaration("M") }
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
