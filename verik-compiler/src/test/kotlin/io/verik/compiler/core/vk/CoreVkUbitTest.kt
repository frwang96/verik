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

package io.verik.compiler.core.vk

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CoreVkUbitTest : BaseTest() {

    @Test
    fun `transform get`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent()
        )
        assertElementEquals(
            "SvArrayAccessExpression(Boolean, KtReferenceExpression(*), ConstantExpression(*))",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform set`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0)
                fun f() {
                    x[0] = true
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBinaryExpression(
                    Unit,
                    SvArrayAccessExpression(Boolean, KtReferenceExpression(*), ConstantExpression(*)),
                    ConstantExpression(*),
                    EQ
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform sll`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x00)
                var y = x sll 1
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Ubit<`8`>, *, *, LTLT)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform srl`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x00)
                var y = x srl 1
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Ubit<`8`>, *, *, GTGT)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform reverse`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x00)
                var y = x.reverse()
            """.trimIndent()
        )
        assertElementEquals(
            "StreamingExpression(Ubit<`8`>, KtReferenceExpression(*))",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform uext`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x0).uext<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "WidthCastExpression(Ubit<`8`>, ConstantExpression(Ubit<`4`>, 4'h0), 8)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform sext`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x0).sext<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(
                    Sbit<`8`>,
                    ${'$'}unsigned,
                    null,
                    [WidthCastExpression(
                        Sbit<`8`>,
                        KtCallExpression(Sbit<`8`>, ${'$'}signed, null, [ConstantExpression(*)], []),
                        8
                    )],
                    []
                )
            """.trimIndent(),
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform tru`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x00).tru<`4`>()
            """.trimIndent()
        )
        assertElementEquals(
            """
                ConstantPartSelectExpression(
                    Ubit<`4`>,
                    ConstantExpression(*),
                    ConstantExpression(Int, 3),
                    ConstantExpression(Int, 0)
                )
            """.trimIndent(),
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform slice`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0x00)
                var y = x.slice<`4`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            """
                ConstantPartSelectExpression(
                    Ubit<`4`>,
                    KtReferenceExpression(*),
                    KtCallExpression(Int, plus, ConstantExpression(*), [ConstantExpression(Int, 3)], []),
                    ConstantExpression(*)
                )
            """.trimIndent(),
            projectContext.findExpression("y")
        )
    }
}
