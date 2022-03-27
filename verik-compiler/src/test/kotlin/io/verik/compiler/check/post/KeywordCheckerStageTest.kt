/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class KeywordCheckerStageTest : BaseTest() {

    @Test
    fun `keyword property`() {
        driveMessageTest(
            """
                class M : Module() {
                    val alias: Boolean = nc()
                }
            """.trimIndent(),
            true,
            "Conflict with SystemVerilog reserved keyword: alias"
        )
    }
}
