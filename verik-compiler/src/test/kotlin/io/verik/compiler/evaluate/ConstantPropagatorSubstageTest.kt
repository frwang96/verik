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

package io.verik.compiler.evaluate

import io.verik.compiler.specialize.SpecializerStage
import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ConstantPropagatorSubstageTest : BaseTest() {

    @Test
    fun `constant expression`() {
        driveElementTest(
            """
                const val x = 0
                fun f() {
                    println(x)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, [ConstantExpression(Int, 0)], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `constant reference expression`() {
        driveElementTest(
            """
                const val x = 0
                const val y = x
                fun f() {
                    println(y)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, [ConstantExpression(Int, 0)], [])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `constant call expression`() {
        driveElementTest(
            """
                val y = b<TRUE>()
                fun f() {
                    println(y)
                }
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(Unit, println, null, [CallExpression(Boolean, b, null, [], [`1`])], [])"
        ) { it.findExpression("f") }
    }
}
