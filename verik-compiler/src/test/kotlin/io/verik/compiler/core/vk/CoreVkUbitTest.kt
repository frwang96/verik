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

package io.verik.compiler.core.vk

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

class CoreVkUbitTest : BaseTest() {

    @Test
    fun `transform get`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent()
        )
        assertElementEquals(
            "SvArrayAccessExpression(Boolean, KtReferenceExpression(*), ConstantExpression(*))",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `transform set`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = u(0)
                fun f() {
                    x[0] = true
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBinaryExpression(
                    Unit,
                    EQ,
                    SvArrayAccessExpression(Boolean, KtReferenceExpression(*), ConstantExpression(*)),
                    ConstantExpression(*)
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }
}
