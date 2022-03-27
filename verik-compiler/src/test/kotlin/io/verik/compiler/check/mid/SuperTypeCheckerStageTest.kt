/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class SuperTypeCheckerStageTest : BaseTest() {

    @Test
    fun `entry parameterized`() {
        driveMessageTest(
            """
                class C
            """.trimIndent(),
            true,
            "Supertype is required: C"
        )
    }
}
