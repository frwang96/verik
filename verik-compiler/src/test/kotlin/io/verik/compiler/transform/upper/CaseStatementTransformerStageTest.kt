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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class CaseStatementTransformerStageTest : BaseTest() {

    @Test
    fun `case statement`() {
        driveElementTest(
            """
                var x = 0
                fun f() {
                    when (x) {
                        0 -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            CaseStatementTransformerStage::class,
            """
                CaseStatement(
                    Unit,
                    ReferenceExpression(*),
                    [CaseEntry([ConstantExpression(Int, 0)], *), CaseEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `if expression`() {
        driveElementTest(
            """
                var x = false
                fun f() {
                    when {
                        x -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            CaseStatementTransformerStage::class,
            """
                IfExpression(
                    Unit,
                    ReferenceExpression(Boolean, x, null),
                    BlockExpression(*),
                    BlockExpression(*)
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `block expression`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("ControlFlowWithEmptyBody")
                    when {}
                }
            """.trimIndent(),
            CaseStatementTransformerStage::class,
            "BlockExpression(Unit, [])"
        ) { it.findExpression("f") }
    }
}
