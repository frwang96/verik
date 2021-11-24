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

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TypeCheckerStageTest : BaseTest() {

    @Test
    fun `expression equals violation`() {
        driveTest(
            """
                var x = u(0x00)
                fun f() {
                    x = u(0)
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`8`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `unary operator violation`() {
        driveTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    x = u<`1`>()
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`4`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `binary operator violation`() {
        driveTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    x = u(0) + u(0)
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`4`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `extension violation`() {
        driveTest(
            """
                var x: Ubit<`8`> = nc()
                fun f() {
                    println(x.ext<`4`>())
                }
            """.trimIndent(),
            true,
            "Unable to extend from Ubit<`8`> to Ubit<`4`>"
        )
    }

    @Test
    fun `truncation violation`() {
        driveTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    println(x.tru<`8`>())
                }
            """.trimIndent(),
            true,
            "Unable to truncate from Ubit<`4`> to Ubit<`8`>"
        )
    }

    @Test
    fun `concatenation violation`() {
        driveTest(
            """
                var x: Ubit<`1`> = cat(false, false)
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`1`> actual Ubit<`2`>"
        )
    }

    @Test
    fun `replication violation`() {
        driveTest(
            """
                var x: Ubit<`1`> = rep<`3`>(false)
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`1`> actual Ubit<`3`>"
        )
    }
}
