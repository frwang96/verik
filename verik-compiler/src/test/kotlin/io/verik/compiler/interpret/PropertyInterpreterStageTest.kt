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

internal class PropertyInterpreterStageTest : BaseTest() {

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
            PropertyInterpreterStage::class,
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
                    val m = optional<FALSE, M0> { M0() }
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
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
            PropertyInterpreterStage::class,
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
            PropertyInterpreterStage::class,
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
            PropertyInterpreterStage::class,
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

    @Test
    fun `interpret property static`() {
        driveElementTest(
            """
                object O {
                    var x = false
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "Property(x, Boolean, *, 1, 1)"
        ) { it.findDeclaration("x") }
    }
}
