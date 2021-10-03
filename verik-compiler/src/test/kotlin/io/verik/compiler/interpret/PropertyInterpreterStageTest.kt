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

internal class PropertyInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret module instantiation`() {
        val projectContext = driveTest(
            PropertyInterpreterStage::class,
            """
                class M(@In var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(false)
                }
            """.trimIndent()
        )
        assertElementEquals(
            "BasicComponentInstantiation(m, M, [PortInstantiation(x, *, INPUT)])",
            projectContext.findDeclaration("m")
        )
    }

    @Test
    fun `interpret module instantiation not connected`() {
        val projectContext = driveTest(
            PropertyInterpreterStage::class,
            """
                class M(@Out var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(nc())
                }
            """.trimIndent()
        )
        assertElementEquals(
            "BasicComponentInstantiation(m, M, [PortInstantiation(x, null, OUTPUT)])",
            projectContext.findDeclaration("m")
        )
    }

    @Test
    fun `interpret module instantiation not connected illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                PropertyInterpreterStage::class,
                """
                    class M(@In var x: Boolean) : Module()
                    class Top : Module() {
                        @Make
                        val m = M(nc())
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Input port not connected: x", message) }
    }

    @Test
    fun `interpret interface instantiation`() {
        val projectContext = driveTest(
            PropertyInterpreterStage::class,
            """
                class I : Interface()
                class Top : Module() {
                    @Make
                    val i = I()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "BasicComponentInstantiation(i, I, [])",
            projectContext.findDeclaration("i")
        )
    }

    @Test
    fun `interpret clocking block instantiation`() {
        val projectContext = driveTest(
            PropertyInterpreterStage::class,
            """
                class CB(override val event: Event) : ClockingBlock()
                class Top : Module() {
                    @Make
                    val cb = CB(posedge(false))
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ClockingBlockInstantiation(cb, CB, [], EventControlExpression(*))",
            projectContext.findDeclaration("cb")
        )
    }

    @Test
    fun `interpret clocking block instantiation illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                PropertyInterpreterStage::class,
                """
                    class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                    class Top : Module() {
                        @Make
                        val cb = CB(posedge(false), false)
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Port instantiation must match port name: x", message) }
    }

    @Test
    fun `interpret property`() {
        val projectContext = driveTest(
            PropertyInterpreterStage::class,
            """
                var x = false
            """.trimIndent()
        )
        assertElementEquals(
            "SvProperty(x, Boolean, *, null)",
            projectContext.findDeclaration("x")
        )
    }
}
