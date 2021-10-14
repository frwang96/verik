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

package io.verik.compiler.transform.mid

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class StringTemplateExpressionReducerStageTest : BaseTest() {

    @Test
    fun `reduce literal entry`() {
        val projectContext = driveTest(
            StringTemplateExpressionReducerStage::class,
            """
                var x = "abc"
            """.trimIndent()
        )
        assertElementEquals(
            "StringExpression(String, abc)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `reduce expression entry`() {
        val projectContext = driveTest(
            StringTemplateExpressionReducerStage::class,
            """
                var x = 0
                var y = "${"$"}x"
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(
                    String,
                    ${"$"}sformatf,
                    null,
                    [StringExpression(String, %d), KtReferenceExpression(Int, x, null)],
                    []
                )
            """.trimIndent(),
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `reduce expression entry escape percent`() {
        val projectContext = driveTest(
            StringTemplateExpressionReducerStage::class,
            """
                var x = "${"$"}{0}%"
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(String, *, null, [StringExpression(String, %d%%), ConstantExpression(*)], [])
            """.trimIndent(),
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `reduce expression entry no escape percent`() {
        val projectContext = driveTest(
            StringTemplateExpressionReducerStage::class,
            """
                var x = "%"
            """.trimIndent()
        )
        assertElementEquals(
            "StringExpression(String, %)",
            projectContext.findExpression("x")
        )
    }
}
