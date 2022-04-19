/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ArrayAccessMutabilityCheckerStageTest : BaseTest() {

    @Test
    fun `array access expression illegal`() {
        driveMessageTest(
            """
                fun f() {
                    val x = u(0b00)
                    x[0] = true
                }
            """.trimIndent(),
            true,
            "Property declared val cannot be reassigned: x"
        )
    }

    @Test
    fun `array access expression nested illegal`() {
        driveMessageTest(
            """
                fun f() {
                    val x = u(0b00)
                    x[2, 1][0] = true
                }
            """.trimIndent(),
            true,
            "Property declared val cannot be reassigned: x"
        )
    }
}
