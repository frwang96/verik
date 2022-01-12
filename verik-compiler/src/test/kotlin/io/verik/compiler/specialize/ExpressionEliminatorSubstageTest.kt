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

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class ExpressionEliminatorSubstageTest : BaseTest() {

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
                class C<N : `*`> {
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
}
