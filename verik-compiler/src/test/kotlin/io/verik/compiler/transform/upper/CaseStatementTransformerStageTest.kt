/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class CaseStatementTransformerStageTest : BaseTest() {

    @Test
    fun `when expression with subject`() {
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
            CaseStatementTransformerStage::class,
            """
                CaseStatement(
                    Unit,
                    ReferenceExpression(Int, x, null, 0),
                    [CaseEntry([ConstantExpression(Int, 0)], *), CaseEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `when expression without subject`() {
        driveElementTest(
            """
                var x = false
                fun f() {
                    when {
                        x -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            CaseStatementTransformerStage::class,
            """
                CaseStatement(
                    Unit,
                    ConstantExpression(Boolean, 1'b1),
                    [CaseEntry([ReferenceExpression(Boolean, x, null, 0)], *), CaseEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `when expression empty`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    when {}
                }
            """.trimIndent(),
            CaseStatementTransformerStage::class,
            "BlockExpression(Unit, [])"
        ) { it.findExpression("f") }
    }
}
