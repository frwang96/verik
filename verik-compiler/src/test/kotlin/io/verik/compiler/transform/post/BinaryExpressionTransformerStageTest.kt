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

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class BinaryExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform plus`() {
        driveElementTest(
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Int, ReferenceExpression(*), ConstantExpression(*), PLUS)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `transform comparison`() {
        driveElementTest(
            """
                @Suppress("SimplifyBooleanWithConstants")
                var x = 0 < 1
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Boolean, *, *, LT)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `transform logical and`() {
        driveElementTest(
            """
                var x = false
                var y = false
                var z = x && y
            """.trimIndent(),
            BinaryExpressionTransformerStage::class,
            "SvBinaryExpression(Boolean, *, *, ANDAND)"
        ) { it.findExpression("z") }
    }
}
