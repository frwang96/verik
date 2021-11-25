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

internal class EnumNameTransformerStageTest : BaseTest() {

    @Test
    fun `string template expression property`() {
        driveElementTest(
            """
                enum class E { A }
                val e = E.A
                fun f() {
                    "${"$"}e"
                }
            """.trimIndent(),
            EnumNameTransformerStage::class,
            "StringTemplateExpression(String, [KtCallExpression(String, name, ReferenceExpression(*), [], [])])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `string template expression enum entry`() {
        driveElementTest(
            """
                enum class E { A }
                fun f() {
                    "${"$"}{E.A}"
                }
            """.trimIndent(),
            EnumNameTransformerStage::class,
            "StringTemplateExpression(String, [StringExpression(String, A)])"
        ) { it.findExpression("f") }
    }

    @Test
    fun `call expression println enum entry`() {
        driveElementTest(
            """
                enum class E { A }
                fun f() {
                    println(E.A)
                }
            """.trimIndent(),
            EnumNameTransformerStage::class,
            "KtCallExpression(Unit, println, null, [StringExpression(String, A)], [])"
        ) { it.findExpression("f") }
    }
}
