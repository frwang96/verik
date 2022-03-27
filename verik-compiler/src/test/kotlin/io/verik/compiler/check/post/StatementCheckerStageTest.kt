/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class StatementCheckerStageTest : BaseTest() {

    @Test
    fun `invalid statement`() {
        driveMessageTest(
            """
                fun f() {
                    0
                }
            """.trimIndent(),
            true,
            "Could not interpret expression as statement"
        )
    }
}
