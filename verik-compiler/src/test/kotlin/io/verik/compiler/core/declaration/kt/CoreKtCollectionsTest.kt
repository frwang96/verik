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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CoreKtCollectionsTest : BaseTest() {

    @Test
    fun `transform forEach until`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                fun f() {
                    @Suppress("ForEachParameterNotUsed")
                    (0 until 8).forEach { }
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                ForStatement(
                    Void,
                    SvValueParameter(it, Int),
                    ConstantExpression(Int, 0),
                    KtCallExpression(Boolean, lt, ReferenceExpression(Int, it, null), [ConstantExpression(*)], []),
                    KtUnaryExpression(Int, ReferenceExpression(*), POST_INC),
                    KtBlockExpression(Function, [])
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }
}
