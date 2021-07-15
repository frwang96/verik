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

internal class CasterExpressionVisitorTest : BaseTest() {

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
    fun `reference expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = x
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(Int, x)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `call expression base function`() {
        val projectContext = TestDriver.cast(
            """
                fun f() {}
                var x = f()
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Unit, f, [], [])",
            projectContext.findExpression("x")
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
    fun `type argument`() {
        val projectContext = TestDriver.cast(
            """
                var x = u<`8`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            "CallExpression(Ubit<`*`>, u, [TypeArgument(null, `8`)], *)",
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
            "CallExpression(Unit, f, [], [ValueArgument(null, *)])",
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
            "CallExpression(Unit, f, [ValueArgument(x, *)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `dot qualified expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent()
        )
        assertElementEquals(
            "DotQualifiedExpression(Int, ReferenceExpression(*), CallExpression(*))",
            projectContext.findExpression("y")
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
            "StringTemplateExpression(String, [ExpressionStringTemplateEntry(ReferenceExpression(*))])",
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
            "IfExpression(Int, ReferenceExpression(*), ConstantExpression(*), ConstantExpression(*))",
            projectContext.findExpression("y")
        )
    }
}