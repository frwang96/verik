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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findDeclaration
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class ExpressionCasterTest : BaseTest() {

    @Test
    fun `block expression empty`() {
        driveTest(
            """
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, KtBlockExpression(Unit, []), [], [], [], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `unary expression prefix`() {
        driveTest(
            """
                var x = !false
            """.trimIndent(),
            CasterStage::class,
            "KtUnaryExpression(Boolean, ConstantExpression(*), EXCL)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `unary expression postfix`() {
        driveTest(
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
        driveTest(
            """
                var x = 0 + 0
            """.trimIndent(),
            CasterStage::class,
            "KtBinaryExpression(Int, ConstantExpression(*), ConstantExpression(*), PLUS)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `binary expression identifier`() {
        driveTest(
            """
                var x = u(1) shl 1
            """.trimIndent(),
            CasterStage::class,
            "KtCallExpression(Ubit<`*`>, shl, KtCallExpression(*), [ConstantExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `reference expression simple`() {
        driveTest(
            """
                var x = 0
                var y = x
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(Int, x, null)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reference expression with package`() {
        driveTest(
            """
                var x = 0
                var y = test.x
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(Int, x, null)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reference expression with class`() {
        driveTest(
            """
                enum class E { A }
                var x = E.A
            """.trimIndent(),
            CasterStage::class,
            "ReferenceExpression(E, A, null)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `call expression simple`() {
        driveTest(
            """
                fun f() {
                    println()
                }
            """.trimIndent(),
            CasterStage::class,
            "KtCallExpression(Unit, println, null, [], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `call expression with receiver`() {
        driveTest(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent(),
            CasterStage::class,
            "KtCallExpression(Int, plus, ReferenceExpression(*), [ConstantExpression(*)], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `call expression with package`() {
        driveTest(
            """
                var x = io.verik.core.random()
            """.trimIndent(),
            CasterStage::class,
            "KtCallExpression(Int, random, null, [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant expression integer`() {
        driveTest(
            """
                var x = 0
            """.trimIndent(),
            CasterStage::class,
            "ConstantExpression(Int, 0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `this expression`() {
        driveTest(
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
        driveTest(
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
            "KtCallExpression(Unit, f, SuperExpression(C), [], [])"
        ) { it.findExpression("g") }
    }

    @Test
    fun `return statement`() {
        driveTest(
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
        driveTest(
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { y -> }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtCallExpression(
                    Unit,
                    forEach,
                    ReferenceExpression(*),
                    [FunctionLiteralExpression(Function, [KtValueParameter(y, Boolean, [], 0, 0)], *)],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `function literal expression property implicit`() {
        driveTest(
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                KtCallExpression(
                    Unit,
                    forEach,
                    ReferenceExpression(*),
                    [FunctionLiteralExpression(Function, [KtValueParameter(it, Boolean, [], 0, 0)], *)],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `array access expression`() {
        driveTest(
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
        driveTest(
            """
                var x = 0 is Int
            """.trimIndent(),
            CasterStage::class,
            "IsExpression(Boolean, ConstantExpression(*), KtProperty(<tmp>, Int, null, [], 0), 0, Int)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `as expression`() {
        driveTest(
            """
                var x = 0 as Int
            """.trimIndent(),
            CasterStage::class,
            "AsExpression(Int, ConstantExpression(*))"
        ) { it.findExpression("x") }
    }

    @Test
    fun `if expression`() {
        driveTest(
            """
                var x = false 
                var y = if (x) 1 else 0
            """.trimIndent(),
            CasterStage::class,
            "IfExpression(Int, ReferenceExpression(*), ConstantExpression(*), ConstantExpression(*))"
        ) { it.findExpression("y") }
    }

    @Test
    fun `while statement`() {
        driveTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    while (true) {}
                }
            """.trimIndent(),
            CasterStage::class,
            "WhileStatement(Unit, ConstantExpression(*), KtBlockExpression(*), 0)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `do while statement`() {
        driveTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    do { true } while (true)
                }
            """.trimIndent(),
            CasterStage::class,
            "WhileStatement(Unit, ConstantExpression(*), KtBlockExpression(*), 1)"
        ) { it.findExpression("f") }
    }

    @Test
    fun `for statement`() {
        driveTest(
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
                    KtValueParameter(y, Boolean, [], 0, 0),
                    ReferenceExpression(*),
                    KtBlockExpression(Unit, [])
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
