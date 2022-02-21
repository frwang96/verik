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
    fun `when expression with subject`() {
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
                    ReferenceExpression(Int, x, null, 0),
                    [CaseEntry([ConstantExpression(Int, 0)], *), CaseEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `when expression without subject`() {
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
                CaseStatement(
                    Unit,
                    ConstantExpression(Boolean, 1'b1),
                    [CaseEntry([ReferenceExpression(Boolean, x, null, 0)], *), CaseEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `when expression empty`() {
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
