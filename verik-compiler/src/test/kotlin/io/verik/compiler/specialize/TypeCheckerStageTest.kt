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

package io.verik.compiler.specialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.driveTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeCheckerStageTest : BaseTest() {

    @Test
    fun `expression equals violation`() {
        assertThrows<TestErrorException> {
            driveTest(
                TypeCheckerStage::class,
                """
                    var x = u(0x00)
                    fun f() {
                        x = u(0)
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Type mismatch: Expected Ubit<`8`> actual Ubit<`1`>", message) }
    }

    @Test
    fun `binary operator violation`() {
        assertThrows<TestErrorException> {
            driveTest(
                TypeCheckerStage::class,
                """
                    var x: Ubit<`4`> = nc()
                    fun f() {
                        x = u(0) + u(0)
                    }
                """.trimIndent()
            )
        }.apply { assertEquals("Type mismatch: Expected Ubit<`4`> actual Ubit<`1`>", message) }
    }

    @Test
    fun `concatenation violation`() {
        assertThrows<TestErrorException> {
            driveTest(
                TypeCheckerStage::class,
                """
                    var x: Ubit<`1`> = cat(false, false)
                """.trimIndent()
            )
        }.apply { assertEquals("Type mismatch: Expected Ubit<`1`> actual Ubit<`2`>", message) }
    }
}
