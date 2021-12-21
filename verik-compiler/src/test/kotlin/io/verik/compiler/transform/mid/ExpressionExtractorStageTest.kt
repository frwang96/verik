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

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class ExpressionExtractorStageTest : BaseTest() {

    @Test
    fun `streaming expression`() {
        driveElementTest(
            """
                var x = u(0)
                fun f() {
                    x = x.reverse() + u(0)
                }
            """.trimIndent(),
            ExpressionExtractorStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Ubit<`1`>, StreamingExpression(*), 0, 0, 0)),
                    KtBinaryExpression(
                        Unit,
                        ReferenceExpression(Ubit<`1`>, x, null),
                        KtCallExpression(Ubit<`1`>, plus, ReferenceExpression(Ubit<`1`>, <tmp>, null), [*], []),
                        EQ
                    )
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }
}
