/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class BinaryExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform plus`() {
        driveElementTest(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Int, ReferenceExpression(*), ConstantExpression(*), PLUS)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `transform comparison`() {
        driveElementTest(
            """
                @Suppress("SimplifyBooleanWithConstants")
                var x = 0 < 1
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Boolean, *, *, LT)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `transform logical and`() {
        driveElementTest(
            """
                var x = false
                var y = false
                var z = x && y
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Boolean, *, *, ANDAND)"
        ) { it.findExpression("z") }
    }
}
