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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ProceduralAssignmentTransformerStageTest : BaseTest() {

    @Test
    fun `com assignment`() {
        driveTextFileTest(
            """
                class M : Module() {
                    @Com
                    var x = false
                }
            """.trimIndent(),
            """
                module M;

                    logic x;

                    always_comb begin : __0
                        x = 1'b0;
                    end : __0

                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `com assignment illegal`() {
        driveMessageTest(
            """
                class M : Module() {
                    @Com var x: Boolean = nc()
                }
            """.trimIndent(),
            true,
            "Expected initializer for combinational assignment"
        )
    }

    @Test
    fun `seq assignment`() {
        driveTextFileTest(
            """
                class M : Module() {
                    @Seq
                    var x = oni(posedge(false)) { false }
                }
            """.trimIndent(),
            """
                module M;

                    logic x;

                    always_ff @(posedge 1'b0) begin : __0
                        x <= 1'b0;
                    end : __0
                
                endmodule : M
            """.trimIndent()
        ) { it.rootPackageTextFiles[0] }
    }

    @Test
    fun `seq assignment illegal`() {
        driveMessageTest(
            """
                class M : Module() {
                    @Seq var x: Boolean = nc()
                }
            """.trimIndent(),
            true,
            "Expected onr expression with return value for sequential assignment"
        )
    }
}
