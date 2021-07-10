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

import io.verik.compiler.ast.element.common.CBlockExpression
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.findExpression
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
            "CBlockExpression(Unit, [])",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `parenthesized expression`() {
        val projectContext = TestDriver.cast(
            """
                var x = (0)
            """.trimIndent()
        )
        assertElementEquals(
            "CParenthesizedExpression(Int, CConstantExpression(*))",
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
            "KBinaryExpression(Int, PLUS, CConstantExpression(*), CConstantExpression(*))",
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
            "CReferenceExpression(Int, x)",
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
            "CCallExpression(Unit, f, [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `call expression core`() {
        val projectContext = TestDriver.cast(
            """
                var x = random(0)
            """.trimIndent()
        )
        assertElementEquals(
            "CCallExpression(Int, random, *)",
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
            "CCallExpression(Unit, f, [CValueArgument(null, *)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    @Disabled
    fun `value argument named`() {
        val projectContext = TestDriver.cast(
            """
                fun f(x: Int) {}
                var x = f(x = 0)
            """.trimIndent()
        )
        assertElementEquals(
            "CCallExpression(Unit, f, [CValueArgument(x, *)])",
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
            "CDotQualifiedExpression(Int, CReferenceExpression(*), CCallExpression(*))",
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
            "CConstantExpression(Int, 0)",
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
                CCallExpression(
                    Unit,
                    forever,
                    [CValueArgument(null, KFunctionLiteralExpression(Function, CBlockExpression(*)))]
                )
            """.trimIndent(),
            (projectContext.findExpression("f") as CBlockExpression).statements[0]
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
            "KStringTemplateExpression(String, [KLiteralStringTemplateEntry(abc)])",
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
            "KStringTemplateExpression(String, [KLiteralStringTemplateEntry($)])",
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
            "KStringTemplateExpression(String, [KExpressionStringTemplateEntry(CReferenceExpression(*))])",
            projectContext.findExpression("y")
        )
    }
}