/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class InjectedExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `injected expression simple`() {
        driveElementTest(
            """
                fun f() {
                    inj("abc")
                }
            """.trimIndent(),
            InjectedExpressionTransformerStage::class,
            "InjectedExpression(Void, [abc])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `injected expression multiline`() {
        driveElementTest(
            """
                fun f() {
                    inji<Unit>(${"\"\"\""}
                        abc
                    ${"\"\"\""}.trimIndent())
                }
            """.trimIndent(),
            InjectedExpressionTransformerStage::class,
            "InjectedExpression(Void, [abc])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `injected expression cover point`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x, "bins b = {1'b0}")
                }
            """.trimIndent(),
            InjectedExpressionTransformerStage::class,
            "CoverPoint(cp, ReferenceExpression(Boolean, x, null, 0), [InjectedExpression(Void, *)])"
        ) { it.findDeclaration("cp") }
    }
}
