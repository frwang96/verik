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

package io.verik.compiler.specialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeResolverStageTest : BaseTest() {

    @Test
    fun `resolve property`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                var x = false
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Boolean, *, [])",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    @Disabled
    fun `resolve property type parameterized`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                class C<N : `*`>
                var c = C<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(c, C<`8`>, KtCallExpression(C<`8`>, <init>, null, [], [`8`]), [])",
            projectContext.findDeclaration("c")
        )
    }

    @Test
    fun `resolve reference expression`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                var x = false
                fun f() {
                    x
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtReferenceExpression(Boolean, x, null)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `resolve reference expression with receiver`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                class S(val x: Ubit<`8`>) : Struct()
                val s = S(u0())
                fun f() {
                    s.x
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtReferenceExpression(Ubit<`8`>, x, *)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `resolve call expression plus`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                val x = u(0x00) + u(0x0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<MAX<`8`,`4`>>, plus, *, *, [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `resolve call expression u`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                val x = u<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<WIDTH<`8`>>, u, null, [], [`8`])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `resolve call expression cat`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                val x = cat(u(0), false)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<ADD<`1`, `1`>>, cat, null, *, [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `resolve call expression cat illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                TypeResolverStage::class,
                """
                    val x = cat(posedge(false))
                """.trimIndent()
            )
        }.apply { assertEquals("Could not get width of type: Event", message) }
    }

    @Test
    fun `resolve call expression function`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                fun f(x: Ubit<`8`>) {}
                val x = f(u0())
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, [KtCallExpression(Ubit<`8`>, u0, null, [], [`8`])], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `resolve return statement`() {
        val projectContext = driveTest(
            TypeResolverStage::class,
            """
                fun f(): Ubit<`8`> {
                    return u0()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ReturnStatement(Nothing, KtCallExpression(Ubit<`8`>, u0, null, [], [`8`]))",
            projectContext.findExpression("f")
        )
    }
}
