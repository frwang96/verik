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

internal class StringTemplateExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `reduce literal entry`() {
        driveElementTest(
            """
                var x = "abc"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            "StringExpression(String, abc)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `reduce expression entry`() {
        driveElementTest(
            """
                var x = 0
                var y = "${"$"}x"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            """
                CallExpression(
                    String, ${"$"}sformatf, null,
                    [StringExpression(String, %0d), ReferenceExpression(Int, x, null)],
                    []
                )
            """.trimIndent()
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce expression entry escape percent`() {
        driveElementTest(
            """
                var x = "${"$"}{0}%"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            """
                CallExpression(String, *, null, [StringExpression(String, %0d%%), ConstantExpression(*)], [])
            """.trimIndent()
        ) { it.findExpression("x") }
    }

    @Test
    fun `reduce expression entry no escape percent`() {
        driveElementTest(
            """
                var x = "%"
            """.trimIndent(),
            StringTemplateExpressionTransformerStage::class,
            "StringExpression(String, %)"
        ) { it.findExpression("x") }
    }
}
