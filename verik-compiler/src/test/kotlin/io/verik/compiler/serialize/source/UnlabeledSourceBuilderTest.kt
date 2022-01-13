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

package io.verik.compiler.serialize.source

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class UnlabeledSourceBuilderTest : BaseTest() {

    @Test
    fun `align properties`() {
        driveTextFileTest(
            """
                var x: Boolean = nc()
                var y: Int = nc()
                var z: Ubit<`8`> = nc()
            """.trimIndent(),
            """
                logic       x;
                int         y;
                logic [7:0] z;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `wrap expression`() {
        driveTextFileTest(
            """
                var aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = 0
                var bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb = 0
                fun f() {
                    var x = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa + bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
                }
            """.trimIndent(),
            """
                int aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = 0;
                int bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb = 0;
                
                function automatic void f();
                    int x;
                    x = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        + bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `wrap property`() {
        driveTextFileTest(
            """
                var aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = 0
                var b = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa + aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa + 1
            """.trimIndent(),
            """
                int aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = 0;
                int b = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa + aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                    + 1;
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }
}
