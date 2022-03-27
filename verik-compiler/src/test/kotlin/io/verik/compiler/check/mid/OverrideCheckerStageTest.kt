/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class OverrideCheckerStageTest : BaseTest() {

    @Test
    fun `function is task`() {
        driveMessageTest(
            """
                open class C0 : Class() {
                    @Task
                    open fun f() {}
                }
                class C1 : C0() {
                    override fun f() {}
                }
            """.trimIndent(),
            true,
            "Function should be annotated with @Task: f"
        )
    }

    @Test
    fun `function not task`() {
        driveMessageTest(
            """
                open class C0 : Class() {
                    open fun f() {}
                }
                class C1 : C0() {
                    @Task
                    override fun f() {}
                }
            """.trimIndent(),
            true,
            "Function should not be annotated with @Task: f"
        )
    }
}
