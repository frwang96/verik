/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ProceduralBlockReferenceCheckerStageTest : BaseTest() {

    @Test
    fun `procedural block illegal reference`() {
        driveMessageTest(
            """
                class M : Module() {
                    @Com
                    fun f() {}
                    @Run
                    fun g() { f() }
                }
            """.trimIndent(),
            true,
            "Illegal reference to procedural block: f"
        )
    }
}
