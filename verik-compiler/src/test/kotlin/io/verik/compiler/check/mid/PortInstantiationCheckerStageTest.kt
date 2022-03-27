/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class PortInstantiationCheckerStageTest : BaseTest() {

    @Test
    fun `input not connected`() {
        driveMessageTest(
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
    fun `input not constant`() {
        driveMessageTest(
            """
                class M(@In val x: Boolean) : Module()
                class Top : Module() {
                    var x: Boolean = nc()
                    @Make
                    val m = M(x)
                }
            """.trimIndent(),
            true,
            "Constant expression expected for input port declared as val: x"
        )
    }

    @Test
    fun `output illegal expression`() {
        driveMessageTest(
            """
                class N(@Out var x: Boolean) : Module()
                class M : Module() {
                    @Make
                    val m = N(false)
                }
            """.trimIndent(),
            true,
            "Illegal expression for output port"
        )
    }

    @Test
    fun `output immutable property`() {
        driveMessageTest(
            """
                class N(@Out var x: Boolean) : Module()
                class M : Module() {
                    private val y: Boolean = nc()
                    @Make
                    val m = N(y)
                }
            """.trimIndent(),
            true,
            "Property assigned by output port must be declared as var: y"
        )
    }
}
