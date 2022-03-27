/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ToStringTransformerStageTest : BaseTest() {

    @Test
    fun `enum property`() {
        driveElementTest(
            """
                enum class E { A }
                val e = E.A
                fun f() {
                    "${"$"}e"
                }
            """.trimIndent(),
            ToStringTransformerStage::class,
            "StringTemplateExpression(String, [CallExpression(String, name, ReferenceExpression(*), 0, [], [])])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `enum entry`() {
        driveElementTest(
            """
                enum class E { A }
                fun f() {
                    "${"$"}{E.A}"
                }
            """.trimIndent(),
            ToStringTransformerStage::class,
            "StringTemplateExpression(String, [StringTemplateExpression(String, [A])])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `class to string`() {
        driveElementTest(
            """
                class C : Class() {
                    override fun toString(): String { return "" }
                }
                val c = C()
                fun f() {
                    println(c)
                }
            """.trimIndent(),
            ToStringTransformerStage::class,
            """
                CallExpression(
                    Unit, println, null, 0,
                    [CallExpression(String, toString, ReferenceExpression(C, c, null, 0), 0, [], [])], []
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
