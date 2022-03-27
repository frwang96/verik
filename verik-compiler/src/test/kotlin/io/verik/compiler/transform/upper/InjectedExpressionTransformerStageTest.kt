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
    fun `injected expression constraint`() {
        driveElementTest(
            """
                class C : Class() {
                    @Cons
                    val c = c("1")
                }
            """.trimIndent(),
            InjectedExpressionTransformerStage::class,
            "Constraint(c, BlockExpression(Unit, [InjectedExpression(Void, [1])]))"
        ) { it.findDeclaration("c") }
    }
}
