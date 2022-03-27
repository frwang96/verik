/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TypeParameterCheckerStageTest : BaseTest() {

    @Test
    fun `parameterized function illegal`() {
        driveMessageTest(
            """
                class C : Class() {
                    fun <T> f() {}
                }
            """.trimIndent(),
            true,
            "Function that is not top level cannot be parameterized: f"
        )
    }
}
