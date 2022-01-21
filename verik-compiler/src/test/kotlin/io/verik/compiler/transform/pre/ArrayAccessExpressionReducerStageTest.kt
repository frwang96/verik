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

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ArrayAccessExpressionReducerStageTest : BaseTest() {

    @Test
    fun `reduce get single argument`() {
        driveElementTest(
            """
                var x = u(0)
                var y = x[0]
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Boolean, get, *, [*], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce get multiple arguments`() {
        driveElementTest(
            """
                var x = u(0)
                var y = x[1, 0]
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Ubit<`*`>, get, *, [*], [])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `reduce set`() {
        driveElementTest(
            """
                var x = ArrayList<Boolean>()
                fun f() {
                    x[0] = true
                }
            """.trimIndent(),
            ArrayAccessExpressionReducerStage::class,
            "CallExpression(Unit, set, *, [*, *], [])"
        ) { it.findExpression("f") }
    }
}
