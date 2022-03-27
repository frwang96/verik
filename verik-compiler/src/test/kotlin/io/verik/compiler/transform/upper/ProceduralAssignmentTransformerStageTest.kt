/*
 * SPDX-License-Identifier: Apache-2.0
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
