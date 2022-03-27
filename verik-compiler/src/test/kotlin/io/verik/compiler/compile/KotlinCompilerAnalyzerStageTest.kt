/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.compile

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

class KotlinCompilerAnalyzerStageTest : BaseTest() {

    @Test
    fun `compile error`() {
        driveMessageTest(
            """
                class C {
                    fun f() {
                        g()
                    }
                }
            """.trimIndent(),
            true,
            "Unresolved reference: g"
        )
    }
}
