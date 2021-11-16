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

internal class FillAssignmentTransformerStageTest : BaseTest() {

    @Test
    fun `fill Ubit Boolean`() {
        val projectContext = driveTest(
            FillAssignmentTransformerStage::class,
            """
                var x = u(0x00)
                fun f() {
                    x = x.fill(0, true)
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBinaryExpression(
                    Unit,
                    SvArrayAccessExpression(Boolean, ReferenceExpression(*), ConstantExpression(*)),
                    ConstantExpression(*),
                    EQ
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `fill Ubit Ubit`() {
        val projectContext = driveTest(
            FillAssignmentTransformerStage::class,
            """
                var x = u(0x00)
                fun f() {
                    x = x.fill(0, u(0x0))
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBinaryExpression(
                    Unit,
                    ConstantPartSelectExpression(
                        Ubit<`4`>,
                        ReferenceExpression(*),
                        KtCallExpression(Int, plus, ConstantExpression(Int, 0), [ConstantExpression(Int, 3)], []),
                        ConstantExpression(Int, 0)
                    ),
                    ConstantExpression(*),
                    EQ
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }
}
