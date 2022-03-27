/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PortCasterTest : BaseTest() {

    @Test
    fun `cast port from ansiPortDeclaration`() {
        driveCasterTest(
            SystemVerilogParser.AnsiPortDeclarationContext::class,
            """
                module m(input x);
                endmodule
            """.trimIndent(),
            "Port(x, SimpleDescriptor(Boolean), INPUT)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast port from inputDeclarationNet`() {
        driveCasterTest(
            SystemVerilogParser.InputDeclarationNetContext::class,
            """
                module m(x);
                    input x;
                endmodule
            """.trimIndent(),
            "Port(x, SimpleDescriptor(Boolean), INPUT)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast port from outputDeclarationNet`() {
        driveCasterTest(
            SystemVerilogParser.OutputDeclarationNetContext::class,
            """
                module m(x);
                    output x;
                endmodule
            """.trimIndent(),
            "Port(x, SimpleDescriptor(Boolean), OUTPUT)"
        ) { it.findDeclaration("x") }
    }
}
