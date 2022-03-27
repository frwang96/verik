/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class UnsupportedModifierCheckerStageTest : BaseTest() {

    @Test
    fun `operator modifier`() {
        driveMessageTest(
            """
                class C {
                    operator fun get(int: Int) {}
                }
            """.trimIndent(),
            true,
            "Modifier operator not supported"
        )
    }

    @Test
    fun `operator vararg`() {
        driveMessageTest(
            """
                fun f(vararg x: Int) {}
            """.trimIndent(),
            true,
            "Modifier vararg not supported"
        )
    }
}
