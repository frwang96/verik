/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.general

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class WrapperSerializerStageTest : BaseTest() {

    @Test
    fun `package wrapper file`() {
        driveTextFileTest(
            """
                class C : Class()
            """.trimIndent(),
            """
                `timescale 1ns / 1ns

                package test_pkg;
                
                typedef class C;
                
                `include "src/test/Test.svh"
                
                endpackage : test_pkg
            """.trimIndent()
        ) { it.wrapperTextFile }
    }

    @Test
    fun `non package wrapper file`() {
        driveTextFileTest(
            """
                class M : Module()
            """.trimIndent(),
            """
                `timescale 1ns / 1ns

                `include "src/test/Test.sv"
            """.trimIndent()
        ) { it.wrapperTextFile }
    }
}
