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

internal class ConstantExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `boolean false`() {
        val projectContext = driveTest(
            ConstantExpressionTransformerStage::class,
            """
                var x = false
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Boolean, 1'b0)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `integer decimal`() {
        val projectContext = driveTest(
            ConstantExpressionTransformerStage::class,
            """
                var x = 1_2
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Int, 12)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `integer hexadecimal`() {
        val projectContext = driveTest(
            ConstantExpressionTransformerStage::class,
            """
                var x = 0xaA_bB
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Int, 43707)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `integer binary`() {
        val projectContext = driveTest(
            ConstantExpressionTransformerStage::class,
            """
                var x = 0b0000_1111
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Int, 15)",
            projectContext.findExpression("x")
        )
    }
}
