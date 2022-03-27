/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ObjectCheckerStageTest : BaseTest() {

    @Test
    fun `struct illegal`() {
        driveMessageTest(
            """
                object S: Struct()
            """.trimIndent(),
            true,
            "Struct must not be declared as object: S"
        )
    }
}
