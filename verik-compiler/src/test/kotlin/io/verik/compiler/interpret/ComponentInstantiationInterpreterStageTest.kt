/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ComponentInstantiationInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret module instantiation`() {
        driveElementTest(
            """
                class M0(@In var x: Boolean) : Module()
                class M1 : Module() {
                    @Make
                    val m = M0(false)
                }
            """.trimIndent(),
            ComponentInstantiationInterpreterStage::class,
            "ComponentInstantiation(m, M0, [ConstantExpression(*)])"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `interpret module instantiation null`() {
        driveElementTest(
            """
                class M0 : Module()
                class M1 : Module() {
                    @Make
                    val m = optional(false) { M0() }
                }
            """.trimIndent(),
            ComponentInstantiationInterpreterStage::class,
            "Module(M1, M1, [], [])"
        ) { it.findDeclaration("M1") }
    }

    @Test
    fun `interpret module interface instantiation`() {
        driveElementTest(
            """
                class MI : ModuleInterface()
                class M : Module() {
                    @Make
                    val mi = MI()
                }
            """.trimIndent(),
            ComponentInstantiationInterpreterStage::class,
            "ComponentInstantiation(mi, MI, [])"
        ) { it.findDeclaration("mi") }
    }

    @Test
    fun `interpret module port instantiation`() {
        driveElementTest(
            """
                class MP : ModulePort()
                class M : Module() {
                    @Make
                    val mp = MP()
                }
            """.trimIndent(),
            ComponentInstantiationInterpreterStage::class,
            "ModulePortInstantiation(mp, MP, [])"
        ) { it.findDeclaration("mp") }
    }

    @Test
    fun `interpret clocking block instantiation`() {
        driveElementTest(
            """
                class CB(override val event: Event) : ClockingBlock()
                class M : Module() {
                    @Make
                    val cb = CB(posedge(false))
                }
            """.trimIndent(),
            ComponentInstantiationInterpreterStage::class,
            "ClockingBlockInstantiation(cb, CB, [], EventControlExpression(*))"
        ) { it.findDeclaration("cb") }
    }

    @Test
    fun `interpret clocking block instantiation illegal`() {
        driveMessageTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class M : Module() {
                    @Make
                    val cb = CB(posedge(false), false)
                }
            """.trimIndent(),
            true,
            "Port instantiation must match port name: x"
        )
    }
}
