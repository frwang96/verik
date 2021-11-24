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
        driveTest(
            """
                class M(@In var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(false)
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "BasicComponentInstantiation(m, M, [PortInstantiation(x, *, INPUT)])"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `interpret module instantiation not connected`() {
        driveTest(
            """
                class M(@Out var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(nc())
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "BasicComponentInstantiation(m, M, [PortInstantiation(x, null, OUTPUT)])"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `interpret module instantiation not connected illegal`() {
        driveTest(
            """
                class M(@In var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(nc())
                }
            """.trimIndent(),
            true,
            "Input port not connected: x"
        )
    }

    @Test
    fun `interpret module interface instantiation`() {
        driveTest(
            """
                class MI : ModuleInterface()
                class Top : Module() {
                    @Make
                    val mi = MI()
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "BasicComponentInstantiation(mi, MI, [])"
        ) { it.findDeclaration("mi") }
    }

    @Test
    fun `interpret module port instantiation`() {
        driveTest(
            """
                class MP : ModulePort()
                class Top : Module() {
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
        driveTest(
            """
                class CB(override val event: Event) : ClockingBlock()
                class Top : Module() {
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
        driveTest(
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class Top : Module() {
                    @Make
                    val cb = CB(posedge(false), false)
                }
            """.trimIndent(),
            true,
            "Port instantiation must match port name: x"
        )
    }

    @Test
    fun `interpret property`() {
        driveTest(
            """
                var x = false
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "SvProperty(x, Boolean, *, 1, null)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `interpret property static`() {
        driveTest(
            """
                object O {
                    var x = false
                }
            """.trimIndent(),
            PropertyInterpreterStage::class,
            "SvProperty(x, Boolean, *, 1, 1)"
        ) { it.findDeclaration("x") }
    }
}
