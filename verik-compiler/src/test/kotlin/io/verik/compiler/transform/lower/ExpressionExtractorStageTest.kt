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

package io.verik.compiler.transform.lower

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ExpressionExtractorStageTest : BaseTest() {

    @Test
    fun `constant part select expression`() {
        driveTextFileTest(
            """
                var x = u(0x00)
                var y = u(0x0)
                fun f() {
                    y = (x + x).tru()
                }
            """.trimIndent(),
            """
                logic [7:0] x = 8'h00;
                logic [3:0] y = 4'b0000;

                function automatic void f();
                    logic [7:0] __0;
                    __0 = x + x;
                    y = __0[3:0];
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `streaming expression`() {
        driveTextFileTest(
            """
                var x = u(0)
                fun f() {
                    x = x.reverse() + u(0)
                }
            """.trimIndent(),
            """
                logic [0:0] x = 1'b0;

                function automatic void f();
                    logic [0:0] __0;
                    __0 = {<<{ x }};
                    x = __0 + 1'b0;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }
}
