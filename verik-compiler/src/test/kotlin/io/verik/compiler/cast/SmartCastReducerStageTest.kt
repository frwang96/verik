/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SmartCastReducerStageTest : BaseTest() {

    @Test
    fun `smart cast simple`() {
        driveElementTest(
            """
                open class C
                class D : C()
                fun f(d: D): Boolean { return false }
                fun g() {
                    val c: C = D()
                    if (c is D) {
                        val x = f(c)
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "CallExpression(Boolean, f, null, 0, [ReferenceExpression(D, <tmp>, null, 0)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `smart cast no receiver`() {
        driveElementTest(
            """
                open class C
                class D : C() { fun f(): Boolean { return false } }
                fun g() {
                    val c: C = D()
                    if (c is D) {
                        val x = c.f()
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "CallExpression(Boolean, f, ReferenceExpression(D, <tmp>, null, 0), 0, [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `smart cast with receiver`() {
        driveElementTest(
            """
                open class C
                class D : C() { fun f(): Boolean { return false } }
                class E { val c: C = D() }
                fun g() {
                    val e = E()
                    if (e.c is D) {
                        val x = e.c.f()
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "CallExpression(Boolean, f, ReferenceExpression(D, <tmp>, null, 0), 0, [], [])"
        ) { it.findExpression("x") }
    }
}
