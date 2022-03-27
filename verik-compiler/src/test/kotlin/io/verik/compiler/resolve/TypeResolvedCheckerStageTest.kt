/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TypeResolvedCheckerStageTest : BaseTest() {

    @Test
    fun `cardinal not resolved`() {
        driveMessageTest(
            """
                val x = u(0).ext<`*`>()
            """.trimIndent(),
            true,
            "Type of expression could not be resolved"
        )
    }

    @Test
    fun `cardinal negative`() {
        driveMessageTest(
            """
                var x: Ubit<SUB<`0`, `1`>> = u0()
            """.trimIndent(),
            true,
            "Cardinal type is negative: Ubit<`-1`>"
        )
    }
}
