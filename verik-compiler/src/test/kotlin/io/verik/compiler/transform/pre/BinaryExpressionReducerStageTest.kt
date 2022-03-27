/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class BinaryExpressionReducerStageTest : BaseTest() {

    @Test
    fun `reduce plus`() {
        driveElementTest(
            """
                var x = 0
                var y = x + 0
            """.trimIndent(),
            BinaryExpressionReducerStage::class,
            "CallExpression(Int, plus, ReferenceExpression(*), 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce nested plus`() {
        driveElementTest(
            """
                var x = 0
                var y = 0
                var z = x + y + 0
            """.trimIndent(),
            BinaryExpressionReducerStage::class,
            """
                CallExpression(
                    Int, plus,
                    CallExpression(Int, plus, ReferenceExpression(*), 0, [ReferenceExpression(*)], []),
                    0, [ConstantExpression(*)], []
                )
            """.trimIndent()
        ) { it.findExpression("z") }
    }
}
