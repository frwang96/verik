/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class CastIndexerStageTest : BaseTest() {

    @Test
    fun `error name unicode`() {
        driveMessageTest(
            """
                @Suppress("ObjectPropertyName")
                val αβγ = 0
            """.trimIndent(),
            true,
            "Illegal name: αβγ"
        )
    }
}
