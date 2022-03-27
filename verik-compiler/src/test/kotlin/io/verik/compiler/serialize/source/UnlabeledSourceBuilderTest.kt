/*
 * SPDX-License-Identifier: Apache-2.0
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
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
    }
}
