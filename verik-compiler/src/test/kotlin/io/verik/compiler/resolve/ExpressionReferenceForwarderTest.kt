/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ExpressionReferenceForwarderTest : BaseTest() {

    @Test
    fun `forward property parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<N : `*`> : Class() {
                    var x: Ubit<N> = nc()
                    var y = x
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Ubit<`8`>, x, null, 0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `forward property parameterized super type`() {
        driveElementTest(
            """
                open class C0<T> : Class() {
                    var x: T = nc()
                }
                class C1 : C0<Int>() {
                    fun f() {
                        x
                    }
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Int, x, null, 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `forward property with receiver parameterised super type`() {
        driveElementTest(
            """
                open class C0<T> : Class() {
                    var x: T = nc()
                }
                class C1 : C0<Int>()
                var c = C1()
                fun f() {
                    c.x
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Int, x, ReferenceExpression(C1, c, null, 0), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `forward function parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<N : `*`> : Class() {
                    fun f(): Boolean { return false }
                    var x = f()
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Boolean, f, null, 0, [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `forward function not parameterized`() {
        driveElementTest(
            """
                class D : Class()
                class C<N : `*`> : Class() {
                    var x = D()
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(D, D, null, 0, [], [])"
        ) { it.findExpression("x") }
    }
}
