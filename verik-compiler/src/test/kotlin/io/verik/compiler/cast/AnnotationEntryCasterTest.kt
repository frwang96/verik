/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class AnnotationEntryCasterTest : BaseTest() {

    @Test
    fun `annotation entry unsupported`() {
        driveMessageTest(
            """
                @Synchronized
                fun f() {}
            """.trimIndent(),
            true,
            "Unsupported annotation: Synchronized"
        )
    }
}
