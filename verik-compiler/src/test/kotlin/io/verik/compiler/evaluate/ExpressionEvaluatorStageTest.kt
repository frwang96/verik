/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ExpressionEvaluatorStageTest : BaseTest() {

    @Test
    fun `constant expression type parameterized`() {
        driveElementTest(
            """
                class C<N : `*`> : Class() {
                    val x = i<N>()
                }
                val y = C<`8`>().x + 1
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ConstantExpression(Int, 9)"
        ) { it.findExpression("y") }
    }
}
