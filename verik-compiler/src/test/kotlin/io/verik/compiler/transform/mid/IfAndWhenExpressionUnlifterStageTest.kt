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

import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class IfAndWhenExpressionUnlifterStageTest : BaseTest() {

    @Test
    fun `unlift if expression`() {
        val projectContext = driveTest(
            IfAndWhenExpressionUnlifterStage::class,
            """
                var x = true
                fun f() {
                    val y = if (x) {
                        println()
                        0
                    } else 1
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBlockExpression(Unit, [
                    PropertyStatement(Unit, TemporaryProperty(Int, null)),
                    IfExpression(
                        Unit,
                        KtReferenceExpression(*),
                        KtBlockExpression(Int, [
                            KtCallExpression(*),
                            KtBinaryExpression(Unit, KtReferenceExpression(Int, <tmp>, null), ConstantExpression(*), EQ)
                        ]),
                        KtBinaryExpression(Unit, KtReferenceExpression(Int, <tmp>, null), ConstantExpression(*), EQ)
                    ),
                    PropertyStatement(Unit, SvProperty(y, Int, KtReferenceExpression(Int, <tmp>, null), false))
                ])
            """.trimIndent(),
            (projectContext.findDeclaration("f") as ESvFunction).body!!
        )
    }

    @Test
    fun `unlift when expression`() {
        val projectContext = driveTest(
            IfAndWhenExpressionUnlifterStage::class,
            """
                var x = true
                fun f() {
                    val y = when (x) {
                        else -> 0
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBlockExpression(Unit, [
                    PropertyStatement(Unit, TemporaryProperty(Int, null)),
                    WhenExpression(
                        Unit,
                        KtReferenceExpression(*),
                        [WhenEntry([], KtBinaryExpression(Unit, KtReferenceExpression(Int, <tmp>, null), *, EQ))]
                    ),
                    PropertyStatement(Unit, SvProperty(y, Int, KtReferenceExpression(Int, <tmp>, null), false))
                ])
            """.trimIndent(),
            (projectContext.findDeclaration("f") as ESvFunction).body!!
        )
    }
}
