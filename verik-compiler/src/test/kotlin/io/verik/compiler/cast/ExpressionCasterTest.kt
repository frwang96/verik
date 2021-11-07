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
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, KtBlockExpression(Unit, []), [], [], [], 0)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `unary expression prefix`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = !false
            """.trimIndent()
        )
        assertElementEquals(
            "KtUnaryExpression(Boolean, ConstantExpression(*), EXCL)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `unary expression postfix`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
                fun f() {
                    x++
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtUnaryExpression(Int, ReferenceExpression(*), POST_INC)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `binary expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0 + 0
            """.trimIndent()
        )
        assertElementEquals(
            "KtBinaryExpression(Int, ConstantExpression(*), ConstantExpression(*), PLUS)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `binary expression identifier`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = u(1) sll 1
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<`*`>, sll, KtCallExpression(*), [ConstantExpression(*)], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `reference expression simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
                var y = x
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(Int, x, null)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `reference expression with package`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
                var y = test.x
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(Int, x, null)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `reference expression with class`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                enum class E { A }
                var x = E.A
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(E, A, null)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `call expression simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {
                    println()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, println, null, [], [])",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `call expression with receiver`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Int, plus, ReferenceExpression(*), [ConstantExpression(*)], [])",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `call expression with package`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = io.verik.core.random()
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Int, random, null, [], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant expression integer`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Int, 0)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `this expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C {
                    fun f() {
                        this
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ThisExpression(C)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `super expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                open class C {
                    fun f() {}
                }
                class D : C() {
                    fun g() {
                        super.f()
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, SuperExpression(C), [], [])",
            projectContext.findExpression("g")
        )
    }

    @Test
    fun `return statement`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {
                    return
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ReturnStatement(Nothing, null)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `function literal expression property explicit`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { y -> }
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(
                    Unit,
                    forEach,
                    ReferenceExpression(*),
                    [FunctionLiteralExpression(Function, [KtValueParameter(y, Boolean, [], 0)], *)],
                    [Boolean]
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `function literal expression property implicit`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                val x: Packed<`8`, Boolean> = nc()
                fun f() {
                    x.forEach { }
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(
                    Unit,
                    forEach,
                    ReferenceExpression(*),
                    [FunctionLiteralExpression(Function, [KtValueParameter(it, Boolean, [], 0)], *)],
                    [Boolean]
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `array access expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent()
        )
        assertElementEquals(
            "KtArrayAccessExpression(Boolean, *, [*])",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `is expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = 0 is Int
            """.trimIndent()
        )
        assertElementEquals(
            "IsExpression(Boolean, ConstantExpression(*), KtProperty(<tmp>, Int, null, [], 0), 0, Int)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `if expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x = false 
                var y = if (x) 1 else 0
            """.trimIndent()
        )
        assertElementEquals(
            "IfExpression(Int, ReferenceExpression(*), ConstantExpression(*), ConstantExpression(*))",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `while expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    while (true) {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "WhileExpression(Unit, ConstantExpression(*), KtBlockExpression(*), 0)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `do while expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    do { true } while (true)
                }
            """.trimIndent()
        )
        assertElementEquals(
            "WhileExpression(Unit, ConstantExpression(*), KtBlockExpression(*), 1)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `for expression`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                var x: Packed<`8`, Boolean> = nc()
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    for (y in x) {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                ForExpression(
                    Unit,
                    KtValueParameter(y, Boolean, [], 0),
                    ReferenceExpression(*),
                    KtBlockExpression(Unit, [])
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }
}
