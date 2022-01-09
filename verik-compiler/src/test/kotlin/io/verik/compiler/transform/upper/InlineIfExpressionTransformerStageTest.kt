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

internal class InlineIfExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform inline if`() {
        driveElementTest(
            """
                var x = true
                var y = if (x) 1 else 0
            """.trimIndent(),
            InlineIfExpressionTransformerStage::class,
            "InlineIfExpression(Int, ReferenceExpression(*), ConstantExpression(*), ConstantExpression(*))"
        ) { it.findExpression("y") }
    }

    @Test
    fun `transform inline if nested`() {
        driveElementTest(
            """
                var x = true
                var y = if (x) 1 else if (x) 2 else 3
            """.trimIndent(),
            InlineIfExpressionTransformerStage::class,
            """
                InlineIfExpression(
                    Int, ReferenceExpression(*), ConstantExpression(*),
                    InlineIfExpression(Int, ReferenceExpression(*), ConstantExpression(*), ConstantExpression(*))
                )
            """.trimIndent()
        ) { it.findExpression("y") }
    }
}