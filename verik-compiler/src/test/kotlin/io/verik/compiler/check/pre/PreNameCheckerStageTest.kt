/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class PreNameCheckerStageTest : BaseTest() {

    @Test
    fun `illegal name`() {
        driveMessageTest(
            """
                @Suppress("ObjectPropertyName")
                const val `???` = 0
            """.trimIndent(),
            true,
            "Illegal name: ???"
        )
    }
}
