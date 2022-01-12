/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.transform.lower

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class ExpressionEvaluatorStageTest : BaseTest() {

    @Test
    fun `evaluate call expression Int plus`() {
        driveElementTest(
            """
                var x = 1 + 1
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ConstantExpression(Int, 2)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `evaluate call expression Ubit plus`() {
        driveElementTest(
            """
                var x = u(1) + u(1)
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ConstantExpression(Ubit<`1`>, 1'b0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `evaluate binary expression Boolean and`() {
        driveElementTest(
            """
                var x = false
                @Suppress("SimplifyBooleanWithConstants")
                var y = false && x
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ConstantExpression(Boolean, 1'b0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `evaluate binary expression Boolean or`() {
        driveElementTest(
            """
                var x = false
                @Suppress("SimplifyBooleanWithConstants")
                var y = x || false
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ReferenceExpression(Boolean, x, null)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `evaluate if expression true`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ConstantConditionIf")
                    if (true) {
                        println()
                    }
                }
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "[CallExpression(Unit, ${'$'}display, null, [], [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `evaluate if expression false`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ConstantConditionIf")
                    if (false) {
                        println()
                    }
                }
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "[KtBlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `evaluate inline if expression`() {
        driveElementTest(
            """
                @Suppress("ConstantConditionIf")
                var x = if (true) 1 else 0
            """.trimIndent(),
            ExpressionEvaluatorStage::class,
            "ConstantExpression(Int, 1)"
        ) { it.findExpression("x") }
    }
}
