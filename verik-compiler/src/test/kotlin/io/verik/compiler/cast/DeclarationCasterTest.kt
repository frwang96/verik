/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class DeclarationCasterTest : BaseTest() {

    @Test
    fun `type alias simple`() {
        driveElementTest(
            """
                typealias X = INC<`7`>
            """.trimIndent(),
            CasterStage::class,
            "TypeAlias(X, INC<`7`>, [])"
        ) { it.findDeclaration("X") }
    }

    @Test
    fun `type alias with type parameter`() {
        driveElementTest(
            """
                typealias X<Y> = INC<Y>
            """.trimIndent(),
            CasterStage::class,
            "TypeAlias(X, INC<Y>, [TypeParameter(Y, Any)])"
        ) { it.findDeclaration("X") }
    }

    @Test
    fun `class simple`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "KtClass(C, C, Any, [], [], PrimaryConstructor(C, C, [], null), 0, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with class`() {
        driveElementTest(
            """
                class C {
                    class D
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C, Any, [],
                    [KtClass(D, D, Any, [], [], PrimaryConstructor(D, D, [], null), 0, 0)],
                    PrimaryConstructor(C, C, [], null), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with function`() {
        driveElementTest(
            """
                class C {
                    fun f() {}
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C, Any, [],
                    [KtFunction(f, Unit, *, [], [], 0)],
                    PrimaryConstructor(C, C, [], null), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with property`() {
        driveElementTest(
            """
                class C {
                    val x = false
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C, Any, [],
                    [Property(x, Boolean, ConstantExpression(*), 0, 0)],
                    PrimaryConstructor(C, C, [], null), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with companion object`() {
        driveElementTest(
            """
                class C { companion object }
            """.trimIndent(),
            CasterStage::class,
            "KtClass(C, C, Any, [], [CompanionObject(Companion, [])], PrimaryConstructor(C, C, [], null), 0, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with type parameter`() {
        driveElementTest(
            """
                class C<T>
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C<T>, Any, [TypeParameter(T, Any)], [],
                    PrimaryConstructor(C, C<T>, [], null), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor`() {
        driveElementTest(
            """
                open class C0<T>(x: Int)
                class C1 : C0<Int>(0)
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C1, C1, C0<Int>, [], [],
                    PrimaryConstructor(
                        C1, C1, [], CallExpression(C0<Int>, C0, null, 0, [ConstantExpression(*)], [Int])
                    ),
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C1") }
    }

    @Test
    fun `class with secondary constructor simple`() {
        driveElementTest(
            """
                class C {
                    @Suppress("ConvertSecondaryConstructorToPrimary")
                    constructor()
                }
            """.trimIndent(),
            CasterStage::class,
            "KtClass(C, C, Any, [], [SecondaryConstructor(C, C, BlockExpression(*), [], null)], null, 0, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with secondary constructor with super type call expression`() {
        driveElementTest(
            """
                open class C0<T>(x: Int)
                class C1 : C0<Ubit<`8`>> {
                    @Suppress("ConvertSecondaryConstructorToPrimary")
                    constructor(x: Int): super(0)
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C1, C1, C0<Ubit<`8`>>, [],
                    [SecondaryConstructor(
                        C1, C1, BlockExpression(*), [KtValueParameter(x, Int, null, 0, 0)],
                        CallExpression(C0<Ubit<`8`>>, C0, null, 0, [ConstantExpression(*)], [Ubit<`8`>])
                    )],
                    null, 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C1") }
    }

    @Test
    fun `class with initializer block`() {
        driveElementTest(
            """
                @Suppress("RedundantEmptyInitializerBlock")
                class C {
                    init {}
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    C, C, Any, [], [InitializerBlock(BlockExpression(*))],
                    PrimaryConstructor(C, C, [], null), 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class enum`() {
        driveElementTest(
            """
                enum class E(val value: Ubit<`4`>) { A(u(0x0)) }
            """.trimIndent(),
            CasterStage::class,
            """
                KtClass(
                    E, E, Enum, [], [EnumEntry(A, E, CallExpression(*))],
                    PrimaryConstructor(E, E, [KtValueParameter(value, Ubit<`4`>, null, 1, 0)], null), 1, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("E") }
    }

    @Test
    fun `object simple`() {
        driveElementTest(
            """
                object O
            """.trimIndent(),
            CasterStage::class,
            "KtClass(O, O, Any, [], [], null, 0, 1)"
        ) { it.findDeclaration("O") }
    }

    @Test
    fun `function simple`() {
        driveElementTest(
            """
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, BlockExpression(*), [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with value parameter`() {
        driveElementTest(
            """
                fun f(x: Int = 0) {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, BlockExpression(*), [KtValueParameter(x, Int, ConstantExpression(*), 0, 0)], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `function with type parameter`() {
        driveElementTest(
            """
                fun <N : `*`> f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [TypeParameter(N, `*`)], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `property simple`() {
        driveElementTest(
            """
                var x = false
            """.trimIndent(),
            CasterStage::class,
            "Property(x, Boolean, ConstantExpression(*), 1, 0)"
        ) { it.findDeclaration("x") }
    }
}
