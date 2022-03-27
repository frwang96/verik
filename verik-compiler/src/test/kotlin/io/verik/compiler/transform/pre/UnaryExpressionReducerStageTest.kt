/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class UnaryExpressionReducerStageTest : BaseTest() {

    @Test
    fun `reduce not`() {
        driveElementTest(
            """
                var x = false
                var y = !x
            """.trimIndent(),
            UnaryExpressionReducerStage::class,
            "CallExpression(Boolean, not, ReferenceExpression(*), 0, [], [])"
        ) { it.findExpression("y") }
    }
}
