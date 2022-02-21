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

package io.verik.compiler.evaluate

import io.verik.compiler.specialize.SpecializerStage
import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class ExpressionEvaluatorSubstageTest : BaseTest() {

    @Test
    fun `binary expression and`() {
        driveElementTest(
            """
                var x = false
                @Suppress("SimplifyBooleanWithConstants")
                var y = false && x
            """.trimIndent(),
            SpecializerStage::class,
            "ConstantExpression(Boolean, 1'b0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `binary expression or`() {
        driveElementTest(
            """
                var x = false
                @Suppress("SimplifyBooleanWithConstants")
                var y = x || false
            """.trimIndent(),
            SpecializerStage::class,
            "ReferenceExpression(Boolean, x, null, 0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `if expression constant`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ConstantConditionIf")
                    if (false) println()
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[BlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `if expression reference expression`() {
        driveElementTest(
            """
                const val x = false
                fun f() {
                    @Suppress("ConstantConditionIf")
                    if (x) println()
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[BlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `if expression reference expression type parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<N : `*`> : Class() {
                    val x = b<N>()
                    fun f() {
                        @Suppress("ConstantConditionIf")
                        if (x) println()
                    }
                }
                val c = C<FALSE>()
            """.trimIndent(),
            SpecializerStage::class,
            "[BlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `if expression binary expression`() {
        driveElementTest(
            """
                var x = false
                const val y = false
                fun f() {
                    @Suppress("KotlinConstantConditions")
                    @Suppress("SimplifyBooleanWithConstants")
                    if (x && y) println()
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[BlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `when expression constant false`() {
        driveElementTest(
            """
                var x = true
                fun f() {
                    @Suppress("SimplifyWhenWithBooleanConstantCondition")
                    when {
                        x -> println()
                        false -> println()
                    }
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[WhenExpression(Unit, null, [WhenEntry([ReferenceExpression(*)], BlockExpression(*))])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `when expression constant true`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("SimplifyWhenWithBooleanConstantCondition")
                    when {
                        true -> println()
                        else -> println()
                    }
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[WhenExpression(Unit, null, [WhenEntry([], BlockExpression(*))])]"
        ) { it.findStatements("f") }
    }

    @Test
    fun `when expression empty`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("SimplifyWhenWithBooleanConstantCondition")
                    when {
                        false -> println()
                    }
                }
            """.trimIndent(),
            SpecializerStage::class,
            "[BlockExpression(Unit, [])]"
        ) { it.findStatements("f") }
    }
}
