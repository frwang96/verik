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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ForEachUnrollTransformerStageTest : BaseTest() {

    @Test
    fun `unroll simple`() {
        driveTextFileTest(
            """
                val x = cluster(2) { it }
                fun f() {
                    var y = 0
                    for (i in 0 until 2) {
                        y += x[i]
                    }
                }
            """.trimIndent(),
            """
                int x_0 = 0;
                int x_1 = 1;

                function automatic void f();
                    int y;
                    y = 0;
                    y = y + x[0];
                    y = y + x[1];
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `unroll with property`() {
        driveTextFileTest(
            """
                val x = cluster(2) { it }
                fun f() {
                    for (i in 0 until 2) {
                        val y = x[i]
                    }
                }
            """.trimIndent(),
            """
                int x_0 = 0;
                int x_1 = 1;

                function automatic void f();
                    int y;
                    y = x[0];
                    y = x[1];
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
