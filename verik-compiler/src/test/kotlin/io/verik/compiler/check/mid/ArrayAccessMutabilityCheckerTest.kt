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

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ArrayAccessMutabilityCheckerTest : BaseTest() {

    @Test
    fun `array access expression illegal`() {
        driveMessageTest(
            """
                fun f() {
                    val x = u(0b00)
                    x[0] = true
                }
            """.trimIndent(),
            true,
            "Property declared val cannot be reassigned: x"
        )
    }

    @Test
    fun `array access expression nested illegal`() {
        driveMessageTest(
            """
                fun f() {
                    val x = u(0b00)
                    x[2, 1][0] = true
                }
            """.trimIndent(),
            true,
            "Property declared val cannot be reassigned: x"
        )
    }
}