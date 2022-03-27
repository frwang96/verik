/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ValueParameterCheckerStageTest : BaseTest() {

    @Test
    fun `value parameter module`() {
        driveMessageTest(
            """
                class M : Module()
                fun f(m: M) {}
            """.trimIndent(),
            true,
            "Illegal value parameter type: M"
        )
    }
}
