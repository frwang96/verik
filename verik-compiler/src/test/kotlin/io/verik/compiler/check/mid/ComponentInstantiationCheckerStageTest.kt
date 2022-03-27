/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ComponentInstantiationCheckerStageTest : BaseTest() {

    @Test
    fun `make annotation out of context`() {
        driveMessageTest(
            """
                class M : Module()
                @Make
                val m = M()
            """.trimIndent(),
            true,
            "Make annotation out of context"
        )
    }

    @Test
    fun `make annotation illegal`() {
        driveMessageTest(
            """
                @Make
                val x = false
            """.trimIndent(),
            true,
            "Make annotation only permitted on component instantiations"
        )
    }

    @Test
    fun `make annotation required`() {
        driveMessageTest(
            """
                class M : Module()
                class N : Module() {
                    val m  = M()
                }
            """.trimIndent(),
            true,
            "Make annotation required"
        )
    }
}
