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

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ConstantExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `boolean false`() {
        driveTest(
            """
                var x = false
            """.trimIndent(),
            ConstantExpressionTransformerStage::class,
            "ConstantExpression(Boolean, 1'b0)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `integer decimal`() {
        driveTest(
            """
                var x = 1_2
            """.trimIndent(),
            ConstantExpressionTransformerStage::class,
            "ConstantExpression(Int, 12)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `integer hexadecimal`() {
        driveTest(
            """
                var x = 0xaA_bB
            """.trimIndent(),
            ConstantExpressionTransformerStage::class,
            "ConstantExpression(Int, 43707)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `integer binary`() {
        driveTest(
            """
                var x = 0b0000_1111
            """.trimIndent(),
            ConstantExpressionTransformerStage::class,
            "ConstantExpression(Int, 15)"
        ) { it.findExpression("x") }
    }
}
