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

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class EnumInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret enum simple`() {
        driveElementTest(
            """
                enum class E { A }
            """.trimIndent(),
            EnumInterpreterStage::class,
            "File([Enum(E, E, null, [A]), EnumEntry(A, E, null)])"
        ) { it.files().first() }
    }

    @Test
    fun `interpret enum with property`() {
        driveElementTest(
            """
                enum class E(val value: Ubit<`4`>) { A(u(0x0)) }
            """.trimIndent(),
            EnumInterpreterStage::class,
            """
                File([
                    Enum(E, E, Property(value, Ubit<`4`>, null, 0, 0), [A]),
                    EnumEntry(A, E, ConstantExpression(Ubit<`4`>, 4'b0000))
                ])
            """.trimIndent()
        ) { it.files().first() }
    }
}
