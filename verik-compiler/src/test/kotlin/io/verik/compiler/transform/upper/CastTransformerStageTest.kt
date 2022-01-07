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

internal class CastTransformerStageTest : BaseTest() {

    @Test
    fun `is expression`() {
        driveElementTest(
            """
                fun f() {
                    0 is Int
                }
            """.trimIndent(),
            CastTransformerStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    KtCallExpression(
                        Boolean, ${'$'}cast, null,
                        [ReferenceExpression(Int, <tmp>, null), ConstantExpression(*)],
                        []
                    )
                ]
            """.trimIndent(),
        ) { it.findStatements("f") }
    }

    @Test
    fun `is not expression`() {
        driveElementTest(
            """
                fun f() {
                    0 !is Int
                }
            """.trimIndent(),
            CastTransformerStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    KtCallExpression(
                        Boolean, not,
                        KtCallExpression(
                            Boolean, ${'$'}cast, null,
                            [ReferenceExpression(Int, <tmp>, null), ConstantExpression(*)],
                            []
                        ),
                        [], []
                    )
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }

    @Test
    fun `as expression`() {
        driveElementTest(
            """
                fun f() {
                    0 as Int
                }
            """.trimIndent(),
            CastTransformerStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Int, null, 0, 0, 0)),
                    IfExpression(
                        Unit,
                        KtCallExpression(Boolean, not, KtCallExpression(Boolean, ${'$'}cast, null, *, []), [], []),
                        KtBlockExpression(Nothing, [KtCallExpression(Nothing, fatal, null, [StringExpression(*)], [])]),
                        null
                    ),
                    ReferenceExpression(Int, <tmp>, null)
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }
}
