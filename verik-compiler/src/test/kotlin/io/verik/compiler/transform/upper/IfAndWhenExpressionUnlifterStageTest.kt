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
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class IfAndWhenExpressionUnlifterStageTest : BaseTest() {

    @Test
    fun `unlift if expression`() {
        driveElementTest(
            """
                var x = true
                fun f() {
                    val y = if (x) {
                        println()
                        0
                    } else 1
                }
            """.trimIndent(),
            IfAndWhenExpressionUnlifterStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    IfExpression(
                        Unit,
                        ReferenceExpression(*),
                        KtBlockExpression(Unit, [
                            KtCallExpression(*),
                            KtBinaryExpression(Unit, ReferenceExpression(Int, <tmp>, null), ConstantExpression(*), EQ)
                        ]),
                        KtBlockExpression(Unit, [
                            KtBinaryExpression(Unit, ReferenceExpression(Int, <tmp>, null), ConstantExpression(*), EQ)
                        ])
                    ),
                    PropertyStatement(Unit, SvProperty(y, Int, ReferenceExpression(Int, <tmp>, null), 0, 0, 0))
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }

    @Test
    fun `unlift when expression`() {
        driveElementTest(
            """
                fun f() {
                    val y = when {
                        else -> 0
                    }
                }
            """.trimIndent(),
            IfAndWhenExpressionUnlifterStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    WhenExpression(Unit, null, [
                        WhenEntry([], KtBlockExpression(
                            Unit, [KtBinaryExpression(Unit, ReferenceExpression(Int, <tmp>, null), *, EQ)]
                        ))
                    ]),
                    PropertyStatement(Unit, SvProperty(y, Int, ReferenceExpression(Int, <tmp>, null), 0, 0, 0))
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }

    @Test
    fun `unlift when expression Nothing type`() {
        driveElementTest(
            """
                fun f() {
                    @Suppress("SimplifyWhenWithBooleanConstantCondition")
                    val y = when {
                        true -> 0
                        else -> fatal()
                    }
                }
            """.trimIndent(),
            IfAndWhenExpressionUnlifterStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    WhenExpression(
                        Unit, null,
                        [*, WhenEntry([], KtBlockExpression(Nothing, [KtCallExpression(Nothing, fatal, null, [], [])]))]
                    ),
                    PropertyStatement(Unit, SvProperty(y, Int, ReferenceExpression(Int, <tmp>, null), 0, 0, 0))
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }
}
