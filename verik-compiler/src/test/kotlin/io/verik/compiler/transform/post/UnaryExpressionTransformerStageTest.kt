/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class UnaryExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform not`() {
        driveElementTest(
            """
                var x = false
                var y = x.not()
            """.trimIndent(),
            UnaryExpressionTransformerStage::class,
            "SvUnaryExpression(Boolean, ReferenceExpression(*), LOGICAL_NEG)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `transform postincrement`() {
        driveElementTest(
            """
                var x = 0
                var y = x++
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvUnaryExpression(Int, ReferenceExpression(*), POST_INC)"
        ) { it.findExpression("y") }
    }
}
