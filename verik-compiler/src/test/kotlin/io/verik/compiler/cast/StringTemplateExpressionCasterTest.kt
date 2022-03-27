/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class StringTemplateExpressionCasterTest : BaseTest() {

    @Test
    fun `literal entry`() {
        driveElementTest(
            """
                var x = "abc"
            """.trimIndent(),
            CasterStage::class,
            "StringTemplateExpression(String, [abc])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `literal entry escaped`() {
        driveElementTest(
            """
                var x = "\$"
            """.trimIndent(),
            CasterStage::class,
            "StringTemplateExpression(String, [$])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `expression entry`() {
        driveElementTest(
            """
                var x = 0
                var y = "${"$"}x"
            """.trimIndent(),
            CasterStage::class,
            "StringTemplateExpression(String, [ReferenceExpression(*)])"
        ) { it.findExpression("y") }
    }
}
