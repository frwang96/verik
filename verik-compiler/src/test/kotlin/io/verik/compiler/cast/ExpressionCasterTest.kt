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

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ExpressionCasterTest : BaseTest() {

    @Test
    fun `block expression empty`() {
        driveElementTest(
            """
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, BlockExpression(Unit, []), [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `unary expression prefix`() {
        driveElementTest(
            """
                var x = !false
            """.trimIndent(),
            CasterStage::class,
            "KtUnaryExpression(Boolean, ConstantExpression(*), EXCL)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `unary expression postfix`() {
        driveElementTest(
            """
                var x = 0
                fun f() {
                    x++
                }
            """.trimIndent(),
            CasterStage::class,
            "KtUnaryExpression(Int, ReferenceExpression(*), POST_INC)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `binary expression`() {
        driveElementTest(
            """
                var x = 0 + 0
            """.trimIndent(),
            CasterStage::class,
            "KtBinaryExpression(Int, ConstantExpression(*), ConstantExpression(*), PLUS)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `binary expression identifier`() {
        driveElementTest(
            """
                var x = u(1) shl 1
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Ubit<`*`>, shl, CallExpression(*), 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `reference expression simple`() {
        driveElementTest(
            """
                var x = 0
                var y = x
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(Int, x, null, 0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reference expression with package`() {
        driveElementTest(
            """
                var x = 0
                var y = test.x
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(Int, x, null, 0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reference expression with class`() {
        driveElementTest(
            """
                enum class E { A }
                var x = E.A
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(E, A, null, 0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `reference expression with enum entry`() {
        driveElementTest(
            """
                enum class E(val value: Ubit<`2`>) { A(u(0b00)) }
                var x = E.A.value
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(Ubit<`*`>, value, ReferenceExpression(E, A, null, 0), 0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `call expression simple`() {
        driveElementTest(
            """
                fun f() {
                    println()
                }
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, println, null, 0, [], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `call expression with receiver`() {
        driveElementTest(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Int, plus, ReferenceExpression(*), 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `call expression with package`() {
        driveElementTest(
            """
                var x = io.verik.core.random()
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Int, random, null, 0, [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `call expression safe access`() {
        driveElementTest(
            """
                class C : Class() {
                    fun f() {}
                }
                val c: C? = nc()
                fun g() {
                    c?.f()
                }
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, ReferenceExpression(C, c, null, 0), 1, [], [])"
        ) { it.findExpression("g") }
    }

    @Test
    fun `constant expression integer`() {
        driveElementTest(
            """
                var x = 0
            """.trimIndent(),
            CasterStage::class,
            "ConstantExpression(Int, 0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant expression null`() {
        driveElementTest(
            """
                val x = null
            """.trimIndent(),
            CasterStage::class,
            "ConstantExpression(Nothing, null)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `this expression`() {
        driveElementTest(
            """
                class C {
                    fun f() {
                        this
                    }
                }
            """.trimIndent(),
            CasterStage::class,
            "ThisExpression(C)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `super expression`() {
        driveElementTest(
            """
                open class C {
                    fun f() {}
                }
                class D : C() {
                    fun g() {
                        super.f()
                    }
                }
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, SuperExpression(C), 0, [], [])"
        ) { it.findExpression("g") }
    }

    @Test
    fun `return statement`() {
        driveElementTest(
            """
                fun f() {
                    return
                }
            """.trimIndent(),
            CasterStage::class,
            "ReturnStatement(Nothing, null)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `function literal expression property explicit`() {
        driveElementTest(
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { y -> }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                CallExpression(
                    Unit, forEach, ReferenceExpression(*), 0,
                    [FunctionLiteralExpression(Function, [KtValueParameter(y, Boolean, null, 0, 0)], *)],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `function literal expression property implicit`() {
        driveElementTest(
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                CallExpression(
                    Unit, forEach, ReferenceExpression(*), 0,
                    [FunctionLiteralExpression(Function, [KtValueParameter(it, Boolean, null, 0, 0)], *)],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `function literal expression with return`() {
        driveElementTest(
            """
                val x = oni(posedge(false)) { false }
            """.trimIndent(),
            CasterStage::class,
            """
                CallExpression(
                    Boolean, oni, null, 0,
                    [
                        CallExpression(*),
                        FunctionLiteralExpression(Function, [], BlockExpression(Boolean, [*]))
                    ],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("x") }
    }

    @Test
    fun `array access expression`() {
        driveElementTest(
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent(),
            CasterStage::class,
            "KtArrayAccessExpression(Boolean, *, [*])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `is expression`() {
        driveElementTest(
            """
                var x = 0 is Int
            """.trimIndent(),
            CasterStage::class,
            "IsExpression(Boolean, ConstantExpression(*), Property(<tmp>, Int, null, 0, 0), 0, Int)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `as expression`() {
        driveElementTest(
            """
                var x = 0 as Int
            """.trimIndent(),
            CasterStage::class,
            "AsExpression(Int, ConstantExpression(*))"
        ) { it.findExpression("x") }
    }

    @Test
    fun `if expression`() {
        driveElementTest(
            """
                var x = false 
                var y = if (x) 1 else 0
            """.trimIndent(),
            CasterStage::class,
            """
                IfExpression(
                    Int,
                    ReferenceExpression(Boolean, x, null, 0),
                    BlockExpression(Int, [ConstantExpression(*)]),
                    BlockExpression(Int, [ConstantExpression(*)])
                )
            """.trimIndent()
        ) { it.findExpression("y") }
    }

    @Test
    fun `while statement`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    while (true) {}
                }
            """.trimIndent(),
            CasterStage::class,
            "WhileStatement(Unit, ConstantExpression(*), BlockExpression(*), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `do while statement`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    do { true } while (true)
                }
            """.trimIndent(),
            CasterStage::class,
            "WhileStatement(Unit, ConstantExpression(*), BlockExpression(*), 1)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `for statement block expression`() {
        driveElementTest(
            """
                var x: Packed<`8`, Boolean> = nc()
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    for (y in x) {}
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtForStatement(
                    Unit,
                    KtValueParameter(y, Boolean, null, 0, 0),
                    ReferenceExpression(*),
                    BlockExpression(Unit, [])
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `for statement call expression`() {
        driveElementTest(
            """
                var x: Packed<`8`, Boolean> = nc()
                fun f() {
                    for (y in x) println()
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtForStatement(
                    Unit,
                    KtValueParameter(y, Boolean, null, 0, 0),
                    ReferenceExpression(*),
                    BlockExpression(Unit, [*])
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
