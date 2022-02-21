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
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class CallExpressionCasterTest : BaseTest() {

    @Test
    fun `type argument explicit`() {
        driveElementTest(
            """
                var x = u(0).ext<`8`>()
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Ubit<`*`>, ext, *, 0, *, [`8`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `type argument implicit`() {
        driveElementTest(
            """
                var x: Ubit<`8`> = u(0).ext()
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Ubit<`*`>, ext, *, 0, *, [`*`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument unnamed`() {
        driveElementTest(
            """
                fun f(x: Int) {}
                var x = f(0)
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, null, 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument named`() {
        driveElementTest(
            """
                fun f(x: Int) {}
                var x = f(x = 0)
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, null, 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument named order reversed`() {
        driveElementTest(
            """
                fun f(x: Int, y: String) {}
                var x = f(y = "", x = 0)
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, null, 0, [ConstantExpression(*), StringTemplateExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument vararg none`() {
        driveElementTest(
            """
                var x = max(0)
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Int, max, null, 0, [ConstantExpression(*)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument vararg multiple`() {
        driveElementTest(
            """
                var x = max(0, 1, 2)
            """.trimIndent(),
            CasterStage::class,
            """
                CallExpression(
                    Int, max, null, 0, [ConstantExpression(*), ConstantExpression(*), ConstantExpression(*)], []
                )
            """.trimIndent()
        ) { it.findExpression("x") }
    }

    @Test
    fun `value argument default`() {
        driveElementTest(
            """
                fun f(x: Int = 0) {}
                var x = f()
            """.trimIndent(),
            CasterStage::class,
            "CallExpression(Unit, f, null, 0, [NothingExpression()], [])"
        ) { it.findExpression("x") }
    }
}
