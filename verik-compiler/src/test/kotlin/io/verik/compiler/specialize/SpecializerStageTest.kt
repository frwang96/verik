/*
 * Copyright (c) 2022 Francis Wang
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

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class SpecializerStageTest : BaseTest() {

    @Test
    fun `specialize class simple`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            SpecializerStage::class,
            "KtClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, C, [], []), null)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `specialize class type parameter cardinal`() {
        driveElementTest(
            """
                class C<X : `*`>
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    C_X_8, C<`8`>, [], [TypeParameter(X, `8`)],
                    [], 0, 0, 0, PrimaryConstructor(C_X_8, C<`8`>, [], [X]), null
                )
            """.trimIndent()
        ) { it.findDeclaration("C_X_8") }
    }

    @Test
    fun `specialize class type parameter cardinal function`() {
        driveElementTest(
            """
                class C<X : `*`>
                val c = C<INC<`7`>>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    C_X_8, C<`8`>, [], [TypeParameter(X, `8`)],
                    [], 0, 0, 0, PrimaryConstructor(C_X_8, C<`8`>, [], [X]), null
                )
            """.trimIndent()
        ) { it.findDeclaration("C_X_8") }
    }

    @Test
    fun `specialize class type parameter class`() {
        driveElementTest(
            """
                class C
                class D<T>
                val d = D<C>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    D_T_C, D<C>, [], [TypeParameter(T, C)],
                    [], 0, 0, 0, PrimaryConstructor(D_T_C, D<C>, [], [T]), null
                )
            """.trimIndent()
        ) { it.findDeclaration("D_T_C") }
    }

    @Test
    fun `specialize class with property parameterized`() {
        driveElementTest(
            """
                class C<X : `*`> {
                    val x: Ubit<X> = nc()
                }
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`8`>, [], CallExpression(*), 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize class with call expression parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<X : `*`> {
                    fun f(): Boolean { return false }
                    val x = f()
                }
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Boolean, [], CallExpression(Boolean, f, null, [], []), 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize class with call expression not parameterized`() {
        driveElementTest(
            """
                class D
                class C<X : `*`> {
                    val x = D()
                }
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, D, [], CallExpression(D, D, null, [], []), 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize function type parameter`() {
        driveElementTest(
            """
                fun <X : `*`> f() {}
                val x = f<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "KtFunction(f_X_8, Unit, BlockExpression(*), [], [TypeParameter(X, `8`)], [], 0)"
        ) { it.findDeclaration("f_X_8") }
    }
}
