/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class UnsupportedElementCheckerStageTest : BaseTest() {

    @Test
    fun `throw expression`() {
        driveMessageTest(
            """
                fun f() { throw IllegalArgumentException() }
            """.trimIndent(),
            true,
            "Throw expression not supported"
        )
    }
}
