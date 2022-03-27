/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ConstructorCasterTest : BaseTest() {

    @Test
    fun `cast constructor from classMethodExternConstructor`() {
        driveCasterTest(
            SystemVerilogParser.ClassMethodExternConstructorContext::class,
            """
                class c;
                    extern function new(logic x);
                endclass
            """.trimIndent(),
            "SvConstructor([SvValueParameter(x, SimpleDescriptor(Boolean), 0)])"
        ) { it.findDeclaration("new") }
    }

    @Test
    fun `cast constructor from classConstructorDeclaration`() {
        driveCasterTest(
            SystemVerilogParser.ClassConstructorDeclarationContext::class,
            """
                class c;
                    function new(logic x);
                    endfunction
                endclass
            """.trimIndent(),
            "SvConstructor([SvValueParameter(x, SimpleDescriptor(Boolean), 0)])"
        ) { it.findDeclaration("new") }
    }
}
