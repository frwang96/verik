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
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class CallExpressionCasterTest : BaseTest() {

    @Test
    fun `type argument explicit`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                var x = u<`8`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<`*`>, u, null, [`8`], *)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `type argument implicit`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                var x: Ubit<`8`> = u(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<`*`>, u, null, [`*`], *)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `value argument unnamed`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f(x: Int) {}
                var x = f(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, [], [ConstantExpression(*)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `value argument named`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f(x: Int) {}
                var x = f(x = 0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, [], [ConstantExpression(*)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `value argument named order reversed`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f(x: Int, y: String) {}
                var x = f(y = "", x = 0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, [], [ConstantExpression(*), StringTemplateExpression(*)])",
            projectContext.findExpression("x")
        )
    }

    @Test
    @Disabled
    fun `value argument default`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f(x: Int = 0) {}
                var x = f()
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, [], [ConstantExpression(*)])",
            projectContext.findExpression("x")
        )
    }
}
