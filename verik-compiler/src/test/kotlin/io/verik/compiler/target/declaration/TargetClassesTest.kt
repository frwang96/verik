/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TargetClassesTest : BaseTest() {

    @Test
    fun `serialize type Boolean`() {
        driveTextFileTest(
            """
                var x: Boolean = nc()
            """.trimIndent(),
            """
                logic x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
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
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `serialize type Queue`() {
        driveTextFileTest(
            """
                var x: Queue<Int> = nc()
            """.trimIndent(),
            """
                int x [$];
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `serialize type DynamicArray`() {
        driveTextFileTest(
            """
                var x: DynamicArray<Int> = nc()
            """.trimIndent(),
            """
                int x [];
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `serialize type AssociativeArray`() {
        driveTextFileTest(
            """
                var x: AssociativeArray<String, Int> = nc()
            """.trimIndent(),
            """
                int x [string];
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `serialize type ArrayList`() {
        driveTextFileTest(
            """
                var x: ArrayList<Boolean> = nc()
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(.E(logic)) x;
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
