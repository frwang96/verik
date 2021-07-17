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

import io.verik.compiler.util.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ExpressionCasterVisitorTest : BaseTest() {

    @Test
    fun `block expression empty`() {
        val projectContext = TestDriver.cast(
            """
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, KtBlockExpression(Unit, []), null)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `unary expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = !false
            """.trimIndent()
        )
        assertElementEquals(
            "KtUnaryExpression(Boolean, EXCL, ConstantExpression(*))",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `binary expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0 + 0
            """.trimIndent()
        )
        assertElementEquals(
            "KtBinaryExpression(Int, PLUS, ConstantExpression(*), ConstantExpression(*))",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `simple name expression simple`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = x
            """.trimIndent()
        )
        assertElementEquals(
            "SimpleNameExpression(Int, x, null)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `simple name expression with package`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = verik.x
            """.trimIndent()
        )
        assertElementEquals(
            "SimpleNameExpression(Int, x, SimpleNameExpression(verik, verik, null))",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `call expression simple`() {
        val projectContext = TestDriver.cast(
            """
                fun f() {
                    println()
                }
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Unit, println, *)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `call expression with receiver`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Int, plus, SimpleNameExpression(*), [], [ValueArgument(*)])",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `call expression with package simple`() {
        val projectContext = TestDriver.cast(
            """
                fun f() {}
                var x = verik.f()
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Unit, f, SimpleNameExpression(verik, verik, null), [], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `call expression with package dot qualified`() {
        val projectContext = TestDriver.cast(
            """
                var x = io.verik.core.random()
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Int, random, SimpleNameExpression(io.verik.core, io.verik.core, null), [], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `type argument`() {
        val projectContext = TestDriver.cast(
            """
                var x = u<`8`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Ubit<`*`>, u, null, [TypeArgument(null, `8`)], *)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `value argument unnamed`() {
        val projectContext = TestDriver.cast(
            """
                fun f(x: Int) {}
                var x = f(0)
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Unit, f, null, [], [ValueArgument(null, *)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    @Disabled
    // TODO support named value arguments
    fun `value argument named`() {
        val projectContext = TestDriver.cast(
            """
                fun f(x: Int) {}
                var x = f(x = 0)
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Unit, f, null, [ValueArgument(x, *)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant expression integer`() {
        val projectContext = TestDriver.cast(
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
    fun `lambda expression`() {
        val projectContext = TestDriver.cast(
            """
                fun f() {
                    forever {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                CallExpression(
                    Unit,
                    forever,
                    null,
                    [],
                    [ValueArgument(null, FunctionLiteralExpression(Function, KtBlockExpression(*)))]
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `string template expression literal entry`() {
        val projectContext = TestDriver.cast(
            """
                var x = "abc"
            """.trimIndent()
        )
        assertElementEquals(
            "StringTemplateExpression(String, [LiteralStringTemplateEntry(abc)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `string template expression literal entry escaped`() {
        val projectContext = TestDriver.cast(
            """
                var x = "\$"
            """.trimIndent()
        )
        assertElementEquals(
            "StringTemplateExpression(String, [LiteralStringTemplateEntry($)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `string template expression expression entry`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = "${"$"}x"
            """.trimIndent()
        )
        assertElementEquals(
            "StringTemplateExpression(String, [ExpressionStringTemplateEntry(SimpleNameExpression(*))])",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `if expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = false 
                var y = if (x) 1 else 0
            """.trimIndent()
        )
        assertElementEquals(
            "IfExpression(Int, SimpleNameExpression(*), ConstantExpression(*), ConstantExpression(*))",
            projectContext.findExpression("y")
        )
    }
}