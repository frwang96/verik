/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class TypeResolverStageTest : BaseTest() {

    @Test
    fun `resolve property`() {
        driveTest(
            """
                var x = false
            """.trimIndent(),
            TypeResolverStage::class,
            "KtProperty(x, Boolean, *, [], 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve property type parameterized`() {
        driveTest(
            """
                class C<N : `*`>
                var c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "KtProperty(c, C<`8`>, KtCallExpression(C<`8`>, <init>, null, [], [`8`]), [], 1)"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `resolve reference expression`() {
        driveTest(
            """
                var x = false
                fun f() {
                    x
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Boolean, x, null)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `resolve reference expression with receiver`() {
        driveTest(
            """
                class S(val x: Ubit<`8`>) : Struct()
                val s = S(u0())
                fun f() {
                    s.x
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Ubit<`8`>, x, *)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `resolve call expression plus`() {
        driveTest(
            """
                val x = u(0x00) + u(0x0)
            """.trimIndent(),
            TypeResolverStage::class,
            "KtCallExpression(Ubit<MAX<`8`,`4`>>, plus, *, *, [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression u`() {
        driveTest(
            """
                val x = u<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "KtCallExpression(Ubit<WIDTH<`8`>>, u, null, [], [`8`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression cat`() {
        driveTest(
            """
                val x = cat(u(0), false)
            """.trimIndent(),
            TypeResolverStage::class,
            "KtCallExpression(Ubit<ADD<`1`, `1`>>, cat, null, *, [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression cat illegal`() {
        driveTest(
            """
                val x = cat(posedge(false))
            """.trimIndent(),
            true,
            "Could not get width of type: Event"
        )
    }

    @Test
    fun `resolve call expression rep`() {
        driveTest(
            """
                val x = rep<`3`>(false)
            """.trimIndent(),
            TypeResolverStage::class,
            "KtCallExpression(Ubit<MUL<`1`, `3`>>, rep, null, *, [`3`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve call expression function`() {
        driveTest(
            """
                fun f(x: Ubit<`8`>) {}
                val x = f(u0())
            """.trimIndent(),
            TypeResolverStage::class,
            "KtCallExpression(Unit, f, null, [KtCallExpression(Ubit<`8`>, u0, null, [], [`8`])], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `resolve return statement`() {
        driveTest(
            """
                fun f(): Ubit<`8`> {
                    return u0()
                }
            """.trimIndent(),
            TypeResolverStage::class,
            "ReturnStatement(Nothing, KtCallExpression(Ubit<`8`>, u0, null, [], [`8`]))"
        ) { it.findExpression("f") }
    }
}
