/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class PortCheckerStageTest : BaseTest() {

    @Test
    fun `output port not mutable`() {
        driveMessageTest(
            """
                class M(@Out val x: Boolean): Module()
            """.trimIndent(),
            true,
            "Output port must be declared as var: x"
        )
    }

    @Test
    fun `module interface port mutable`() {
        driveMessageTest(
            """
                class MI: ModuleInterface()
                class M(var mi: ModuleInterface): Module()
            """.trimIndent(),
            true,
            "Module interface port must be declared as val: mi"
        )
    }

    @Test
    fun `port no directionality`() {
        driveMessageTest(
            """
                class M(var x: Boolean): Module()
            """.trimIndent(),
            true,
            "Could not determine directionality of port: x"
        )
    }
}
