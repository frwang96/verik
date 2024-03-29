/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class TypeResolverStageTest : BaseTest() {

    @Test
    fun `resolve property`() {
        driveElementTest(
            """
                var x = false
            """.trimIndent(),
            TypeResolverStage::class,
            "Property(x, Boolean, *, 1, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve property type parameterized`() {
        driveElementTest(
            """
                class C<N : `*`> : Class()
                var c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "Property(c, C<`8`>, CallExpression(C<`8`>, C_N_8, null, 0, [], []), 1, 0)"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `resolve property type parameterized conditional`() {
        driveElementTest(
            """
                var x = if (b<TRUE>()) u(0x0) else u(0x00)
            """.trimIndent(),
            TypeResolverStage::class,
            "Property(x, Ubit<`4`>, *, 1, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve reference expression`() {
        driveElementTest(
            """
                var x = false
                fun f() {
                    x
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Boolean, x, null, 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `resolve reference expression with receiver`() {
        driveElementTest(
            """
                class S(val x: Ubit<`8`>) : Struct()
                val s = S(u0())
                fun f() {
                    s.x
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Ubit<`8`>, x, *, 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `resolve call expression plus`() {
        driveElementTest(
            """
                var x = u(0x0)
                val y = u(0x00) + x
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Ubit<`8`>, plus, *, 0, *, [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `resolve call expression u`() {
        driveElementTest(
            """
                val x = u<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Ubit<`4`>, u, null, 0, [], [`8`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression cat`() {
        driveElementTest(
            """
                val x = cat(u(0), false)
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Ubit<`2`>, cat, null, 0, *, [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression function`() {
        driveElementTest(
            """
                fun f(x: Ubit<`8`>) {}
                val x = f(u0())
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Unit, f, null, 0, [CallExpression(Ubit<`8`>, u0, null, 0, [], [`8`])], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression constructor`() {
        driveElementTest(
            """
                val x = ArrayList<Ubit<`8`>>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(ArrayList<Ubit<`8`>>, ArrayList, null, 0, [], [Ubit<`8`>])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve return statement`() {
        driveElementTest(
            """
                fun f(): Ubit<`8`> {
                    return u0()
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReturnStatement(Nothing, CallExpression(Ubit<`8`>, u0, null, 0, [], [`8`]))"
        ) { it.findExpression("f") }
    }
}
