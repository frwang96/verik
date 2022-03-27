/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class NameRedeclarationCheckerStageTest : BaseTest() {

    @Test
    fun `redeclaration in package`() {
        driveMessageTest(
            """
                enum class E { A }
                class A : Class()
            """.trimIndent(),
            true,
            "Name has already been declared: A"
        )
    }
}
