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

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class DeclarationSpecializerStageTest : BaseTest() {

    @Test
    fun `specialize class type parameter cardinal`() {
        driveElementTest(
            """
                class C<X : `*`>
                val c = C<`8`>()
            """.trimIndent(),
            DeclarationSpecializerStage::class,
            "KtClass(C_X_8, C_X_8, [], [], [], 0, 0, 0, PrimaryConstructor(C_X_8, [], []), null)"
        ) { it.findDeclaration("C_X_8") }
    }

    @Test
    fun `specialize class type parameter cardinal function`() {
        driveElementTest(
            """
                class C<X : `*`>
                val c = C<INC<`7`>>()
            """.trimIndent(),
            DeclarationSpecializerStage::class,
            "KtClass(C_X_8, C_X_8, [], [], [], 0, 0, 0, PrimaryConstructor(C_X_8, [], []), null)"
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
            DeclarationSpecializerStage::class,
            "KtClass(D_T_C, D_T_C, [], [], [], 0, 0, 0, PrimaryConstructor(D_T_C, [], []), null)"
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
            DeclarationSpecializerStage::class,
            "KtProperty(x, Ubit<`8`>, KtCallExpression(*), [], 0)"
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
            DeclarationSpecializerStage::class,
            "KtProperty(x, Boolean, KtCallExpression(Boolean, f, null, [], []), [], 0)"
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
            DeclarationSpecializerStage::class,
            "KtProperty(x, D, KtCallExpression(D, <init>, null, [], []), [], 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize function type parameter`() {
        driveElementTest(
            """
                fun <X : `*`> f() {}
                val x = f<`8`>()
            """.trimIndent(),
            DeclarationSpecializerStage::class,
            "KtFunction(f_X_8, Unit, KtBlockExpression(*), [], [], [], 0)"
        ) { it.findDeclaration("f_X_8") }
    }

    @Test
    fun `specialize property type parameter`() {
        driveElementTest(
            """
                class C
                class D<E> {
                    val e : E = nc()
                }
                val d = D<C>()
            """.trimIndent(),
            DeclarationSpecializerStage::class,
            "KtProperty(e, C, KtCallExpression(*), [], 0)"
        ) { it.findDeclaration("e") }
    }
}
