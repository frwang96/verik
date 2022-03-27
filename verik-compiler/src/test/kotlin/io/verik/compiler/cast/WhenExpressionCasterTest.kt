/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class WhenExpressionCasterTest : BaseTest() {

    @Test
    fun `when expression`() {
        driveElementTest(
            """
                var x = 0
                fun f() {
                    when (x) {
                        0 -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                WhenExpression(
                    Unit,
                    ReferenceExpression(*),
                    [WhenEntry([ConstantExpression(Int, 0)], *), WhenEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
