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

internal class UnaryExpressionTransformerStageTest : BaseTest() {

    @Test
    fun `transform not`() {
        val projectContext = driveTest(
            UnaryExpressionTransformerStage::class,
            """
                var x = false
                var y = x.not()
            """.trimIndent()
        )
        assertElementEquals(
            "SvUnaryExpression(Boolean, KtReferenceExpression(*), EXCL)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform postincrement`() {
        val projectContext = driveTest(
            BinaryExpressionTransformerStage::class,
            """
                var x = 0
                var y = x++
            """.trimIndent()
        )
        assertElementEquals(
            "SvUnaryExpression(Int, KtReferenceExpression(*), POST_INC)",
            projectContext.findExpression("y")
        )
    }
}
