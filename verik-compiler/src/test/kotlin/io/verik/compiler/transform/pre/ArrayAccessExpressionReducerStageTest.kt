/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ArrayAccessExpressionReducerStageTest : BaseTest() {

    @Test
    fun `reduce get single argument`() {
        driveElementTest(
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Boolean, get, *, 0, [*], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce get multiple arguments`() {
        driveElementTest(
            """
                var x = u(0)
                var y = x[1, 0]
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Ubit<`*`>, get, *, 0, [*], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce set`() {
        driveElementTest(
            """
                var x = ArrayList<Boolean>()
                fun f() {
                    x[0] = true
                }
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Unit, set, *, 0, [*, *], [])"
        ) { it.findExpression("f") }
    }
}
