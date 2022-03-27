/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class AssignmentOperatorReducerStageTest : BaseTest() {

    @Test
    fun `reduce plus eq`() {
        driveElementTest(
            """
                var x = 0
                fun f() {
                    x += 1
                }
            """.trimIndent(),
            AssignmentOperatorReducerStage::class,
            """
                KtBinaryExpression(
                    Unit,
                    ReferenceExpression(*),
                    KtBinaryExpression(Int, ReferenceExpression(*), ConstantExpression(*), PLUS),
                    EQ
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
