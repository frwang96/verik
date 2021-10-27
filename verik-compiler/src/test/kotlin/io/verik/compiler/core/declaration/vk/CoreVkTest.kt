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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CoreVkTest : BaseTest() {

    @Test
    fun `transform nc illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                FunctionTransformerStage::class,
                """
                    var x = false
                    fun f() {
                        x = nc()
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Expression used out of context: nc", message) }
    }

    @Test
    fun `transform u`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u<`8`>()
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`4`>, 4'h8)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform u0`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x: Ubit<`8`> = u0()
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`8`>, 8'h00)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform cat`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                val x = cat(u(0), u(0))
            """.trimIndent()
        )
        assertElementEquals(
            "ConcatenationExpression(Ubit<`2`>, [*, *])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform cat illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                FunctionTransformerStage::class,
                """
                val x = cat(u(0))
                """.trimIndent()
            )
        }.apply { assertEquals("Concatenation expects at least two arguments", message) }
    }

    @Test
    fun `transform rep`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                val x = rep<`3`>(false)
            """.trimIndent()
        )
        assertElementEquals(
            "ReplicationExpression(Ubit<`3`>, *, 3)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform random`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                val x = random()
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Int, \$random, null, [], [])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform forever`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                fun f() {
                    forever {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ForeverStatement(Void, KtBlockExpression(*))",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform wait`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = false
                fun f() {
                    wait(posedge(x))
                }
            """.trimIndent()
        )
        assertElementEquals(
            "EventControlExpression(Void, EdgeExpression(Event, *, *))",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform delay`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = false
                fun f() {
                    delay(1)
                }
            """.trimIndent()
        )
        assertElementEquals(
            "DelayExpression(Void, ConstantExpression(*))",
            projectContext.findExpression("f")
        )
    }
}
