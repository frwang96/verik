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

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ParenthesisInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `binary expression same kind left`() {
        driveTextFileTest(
            """
                var x = 0
                var y = (x + 1) + x
            """.trimIndent(),
            """
                int x = 0;
                int y = x + 1 + x;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `binary expression same kind right`() {
        driveTextFileTest(
            """
                var x = 0
                var y = x + (1 + x)
            """.trimIndent(),
            """
                int x = 0;
                int y = x + (1 + x);
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `binary expression different kind`() {
        driveTextFileTest(
            """
                var x = 0
                var y = (x * 2) + x
            """.trimIndent(),
            """
                int x = 0;
                int y = (x * 2) + x;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression same kind left`() {
        driveTextFileTest(
            """
                var x = false
                @Suppress("RedundantIf")
                var y = if (if (x) true else false) 3 else 4
            """.trimIndent(),
            """
                logic x = 1'b0;
                int   y = (x ? 1'b1 : 1'b0) ? 3 : 4;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression same kind right`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                var z = if (x) 1 else if (y) 2 else 3
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                int   z = x ? 1 : y ? 2 : 3;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `inline if expression different kind`() {
        driveTextFileTest(
            """
                var x = false
                var y = false
                var z = if (x && y) 1 else 0
            """.trimIndent(),
            """
                logic x = 1'b0;
                logic y = 1'b0;
                int   z = (x && y) ? 1 : 0;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }
}
