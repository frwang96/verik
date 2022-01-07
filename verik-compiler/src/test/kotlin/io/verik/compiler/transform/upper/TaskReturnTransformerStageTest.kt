/*
 * Copyright (c) 2022 Francis Wang
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

internal class TaskReturnTransformerStageTest : BaseTest() {

    @Test
    fun `internal return`() {
        driveElementTest(
            """
                @Task
                fun f(): Boolean { return false }
            """.trimIndent(),
            TaskReturnTransformerStage::class,
            """
                [
                    KtBinaryExpression(Unit, ReferenceExpression(Boolean, __return, null), ConstantExpression(*), EQ),
                    ReturnStatement(Unit, null)
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }

    @Test
    fun `external return`() {
        driveElementTest(
            """
                var x = false
                @Task
                fun f(): Boolean { return false }
                @Task
                fun g() { x = f() }
            """.trimIndent(),
            TaskReturnTransformerStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(<tmp>, Boolean, null, 0, 1, 0)),
                    KtCallExpression(Boolean, f, null, [ReferenceExpression(Boolean, <tmp>, null)], []),
                    KtBinaryExpression(Unit, ReferenceExpression(*), ReferenceExpression(Boolean, <tmp>, null), EQ)
                ]
            """.trimIndent()
        ) { it.findStatements("g") }
    }
}
