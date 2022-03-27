/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ModuleCasterTest : BaseTest() {

    @Test
    fun `cast module from moduleDeclarationNonAnsi`() {
        driveCasterTest(
            SystemVerilogParser.ModuleDeclarationNonAnsiContext::class,
            """
                module m(x);
                    input x;
                endmodule
            """.trimIndent(),
            "Module(m, [], [], [Port(x, SimpleDescriptor(Boolean), INPUT)])"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `cast module from moduleDeclarationAnsi`() {
        driveCasterTest(
            SystemVerilogParser.ModuleDeclarationAnsiContext::class,
            """
                module m;
                endmodule
            """.trimIndent(),
            "Module(m, [], [], [])"
        ) { it.findDeclaration("m") }
    }
}
