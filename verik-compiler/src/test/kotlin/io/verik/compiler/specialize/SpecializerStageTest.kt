/*
 * SPDX-License-Identifier: Apache-2.0
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
                class C : Class()
            """.trimIndent(),
            SpecializerStage::class,
            "KtClass(C, C, Class, [], [], PrimaryConstructor(C, C, [], CallExpression(*)), 0, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `specialize class type parameter cardinal`() {
        driveElementTest(
            """
                class C<N : `*`> : Class()
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    C_N_8, C<`8`>, Class, [TypeParameter(N, `8`)], [],
                    PrimaryConstructor(C_N_8, C<`8`>, [], CallExpression(*)), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C_N_8") }
    }

    @Test
    fun `specialize class type parameter cardinal function`() {
        driveElementTest(
            """
                class C<N : `*`> : Class()
                val c = C<INC<`7`>>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    C_N_8, C<`8`>, Class, [TypeParameter(N, `8`)], [],
                    PrimaryConstructor(C_N_8, C<`8`>, [], CallExpression(*)), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C_N_8") }
    }

    @Test
    fun `specialize class type parameter class`() {
        driveElementTest(
            """
                class C : Class()
                class D<T> : Class()
                val d = D<C>()
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    D_T_C, D<C>, Class, [TypeParameter(T, C)], [],
                    PrimaryConstructor(D_T_C, D<C>, [], CallExpression(*)), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("D_T_C") }
    }

    @Test
    fun `specialize class with property parameterized`() {
        driveElementTest(
            """
                class C<N : `*`> : Class() {
                    val x: Ubit<N> = nc()
                }
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`8`>, CallExpression(*), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize class with call expression parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<N : `*`> : Class() {
                    fun f(): Boolean { return false }
                    val x = f()
                }
                val c = C<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Boolean, CallExpression(Boolean, f, null, 0, [], []), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize class with call expression not parameterized`() {
        driveElementTest(
            """
                class C0 : Class()
                class C1<N : `*`> : Class() {
                    val x = C0()
                }
                val c = C1<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, C0, CallExpression(C0, C0, null, 0, [], []), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `specialize class nested class`() {
        driveElementTest(
            """
                class C0 : Class() {
                    class C1 : Class()
                }
            """.trimIndent(),
            SpecializerStage::class,
            """
                KtClass(
                    C0, C0, Class, [],
                    [KtClass(C1, C1, Class, [], [], PrimaryConstructor(C1, C1, [], CallExpression(*)), 0, 0)],
                    PrimaryConstructor(C0, C0, [], CallExpression(*)), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C0") }
    }

    @Test
    fun `specialize function type parameter`() {
        driveElementTest(
            """
                fun <N : `*`> f() {}
                val x = f<`8`>()
            """.trimIndent(),
            SpecializerStage::class,
            "KtFunction(f_N_8, Unit, BlockExpression(*), [], [TypeParameter(N, `8`)], 0)"
        ) { it.findDeclaration("f_N_8") }
    }
}
