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
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ConstantExpressionEvaluatorStageTest : BaseTest() {

    @Test
    fun `evaluate Int plus`() {
        driveElementTest(
            """
                var x = 1 + 1
            """.trimIndent(),
            ConstantExpressionEvaluatorStage::class,
            "ConstantExpression(Int, 2)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `evaluate Ubit plus`() {
        driveElementTest(
            """
                var x = u(1) + u(1)
            """.trimIndent(),
            ConstantExpressionEvaluatorStage::class,
            "ConstantExpression(Ubit<`1`>, 1'h0)"
        ) { it.findExpression("x") }
    }
}
