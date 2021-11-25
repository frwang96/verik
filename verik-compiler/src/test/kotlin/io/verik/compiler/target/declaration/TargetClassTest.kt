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

package io.verik.compiler.target.declaration

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TargetClassTest : BaseTest() {

    @Test
    fun `serialize type Boolean`() {
        driveTextFileTest(
            """
                var x: Boolean = nc()
            """.trimIndent(),
            """
                logic x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize type Ubit`() {
        driveTextFileTest(
            """
                var x: Ubit<`8`> = nc()
            """.trimIndent(),
            """
                logic [7:0] x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize type Sbit`() {
        driveTextFileTest(
            """
                var x: Sbit<`8`> = nc()
            """.trimIndent(),
            """
                logic signed [7:0] x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize type Packed`() {
        driveTextFileTest(
            """
                var x: Packed<`8`, Boolean> = nc()
            """.trimIndent(),
            """
                logic [7:0] x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize type Unpacked`() {
        driveTextFileTest(
            """
                var x: Unpacked<`8`, Boolean> = nc()
            """.trimIndent(),
            """
                logic x [7:0];
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }

    @Test
    fun `serialize type ArrayList`() {
        driveTextFileTest(
            """
                var x: ArrayList<Boolean> = nc()
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(logic) x;
            """.trimIndent()
        ) { it.basicPackageTextFiles[0] }
    }
}
