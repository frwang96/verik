/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TypeArgumentTypeCheckerStageTest : BaseTest() {

    @Test
    fun `cardinal type expected`() {
        driveMessageTest(
            """
                var x: Ubit<ADD<`8`, Int>> = u(0)
            """.trimIndent(),
            true,
            "Cardinal type expected but found: Int"
        )
    }

    @Test
    fun `cardinal type expected type parameter`() {
        driveMessageTest(
            """
                class C<N> : Class() {
                    var x: Ubit<INC<N>> = u(0)
                }
            """.trimIndent(),
            true,
            "Cardinal type expected but found: N"
        )
    }
}
