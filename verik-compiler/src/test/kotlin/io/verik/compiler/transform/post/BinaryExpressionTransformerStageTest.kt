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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class BinaryExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform plus`() {
        val projectContext = driveTest(
            BinaryExpressionTransformerStage::class,
            """
                var x = 0
                var y = x.plus(1)
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Int, KtReferenceExpression(*), ConstantExpression(*), PLUS)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform comparison`() {
        val projectContext = driveTest(
            BinaryExpressionTransformerStage::class,
            """
                @Suppress("SimplifyBooleanWithConstants")
                var x = 0 < 1
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Boolean, *, *, LT)",
            projectContext.findExpression("x")
        )
    }
}
