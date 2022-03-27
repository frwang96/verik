/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class StringTemplateExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `reduce literal entry`() {
        driveElementTest(
            """
                var x = "abc"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            "StringExpression(String, abc)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `reduce expression entry`() {
        driveElementTest(
            """
                var x = 0
                var y = "${"$"}x"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            """
                CallExpression(
                    String, ${"$"}sformatf, null, 0,
                    [StringExpression(String, %0d), ReferenceExpression(Int, x, null, 0)],
                    []
                )
            """.trimIndent()
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce expression entry escape percent`() {
        driveElementTest(
            """
                var x = "${"$"}{0}%"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            """
                CallExpression(String, *, null, 0, [StringExpression(String, %0d%%), ConstantExpression(*)], [])
            """.trimIndent()
        ) { it.findExpression("x") }
    }

    @Test
    fun `reduce expression entry no escape percent`() {
        driveElementTest(
            """
                var x = "%"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            "StringExpression(String, %)"
        ) { it.findExpression("x") }
    }
}
