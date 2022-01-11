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

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class OptionalReducerSubstageTest : BaseTest() {

    @Test
    fun `reduce true`() {
        driveElementTest(
            """
                class M : Module()
                val m = optional<TRUE, M> { M() }
            """.trimIndent(),
            SpecializerStage::class,
            "KtProperty(m, M, KtCallExpression(M, <init>, null, [], []), [], 0)"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `reduce false`() {
        driveElementTest(
            """
                class M : Module()
                val m = optional<FALSE, M> { M() }
            """.trimIndent(),
            SpecializerStage::class,
            "KtProperty(m, Nothing, NullExpression(), [], 0)"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `illegal not direct assignment`() {
        driveMessageTest(
            """
                class M : Module()
                var m: M? = nc()
                fun f() {
                    m = optional<TRUE, M> { M() }
                }
            """.trimIndent(),
            true,
            "Optional must be directly assigned to a property"
        )
    }

    @Test
    fun `illegal not val`() {
        driveMessageTest(
            """
                class M : Module()
                var m = optional<TRUE, M> { M() }
            """.trimIndent(),
            true,
            "Property assigned as optional must be declared as val"
        )
    }
}
