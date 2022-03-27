/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ForStatementReducerStageTest : BaseTest() {

    @Test
    fun `reduce for expression`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    for (i in 0 until 8) {}
                }
            """.trimIndent(),
            ForStatementReducerStage::class,
            """
                CallExpression(
                    Unit, forEach, CallExpression(IntRange, until, *, 0, [*], []), 0,
                    [FunctionLiteralExpression(Function, [KtValueParameter(*)], BlockExpression(*))],
                    [Int]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
