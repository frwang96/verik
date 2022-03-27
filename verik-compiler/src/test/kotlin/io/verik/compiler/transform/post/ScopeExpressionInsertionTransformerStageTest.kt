/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ScopeExpressionInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `reference target declaration`() {
        driveElementTest(
            """
                fun f() {
                    ArrayList<Boolean>()
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            """
                CallExpression(
                    ArrayList<Boolean>, __new,
                    ScopeExpression(Void, ArrayList<Boolean>, []),
                    0, [], [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference package property`() {
        driveElementTest(
            """
                var x = false
                class M : Module() {
                    fun f() {
                        x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, test_pkg, []), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference package class`() {
        driveElementTest(
            """
                object O : Class() {
                    var x = false
                }
                class M : Module() {
                    fun f() {
                        O.x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, O, []), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference object property`() {
        driveElementTest(
            """
                object O : Class() {
                    var x = false
                }
                fun f() {
                    O.x
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, O, []), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference module property`() {
        driveElementTest(
            """
                object M0 : Module() {
                    val x: Boolean = nc()
                }
                class M1 : Module() {
                    fun f() {
                        M0.x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            """
                ReferenceExpression(
                    Boolean, x, ReferenceExpression(M0, M0, ReferenceExpression(Void, ${'$'}root, null, 0), 0), 0
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
