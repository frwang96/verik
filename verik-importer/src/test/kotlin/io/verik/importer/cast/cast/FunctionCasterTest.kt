/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class FunctionCasterTest : BaseTest() {

    @Test
    fun `cast function from classMethodFunction`() {
        driveCasterTest(
            SystemVerilogParser.ClassMethodFunctionContext::class,
            """
                class c;
                    static function void f();
                    endfunction
                endclass
            """.trimIndent(),
            "SvFunction(f, [], SimpleDescriptor(Unit), 1)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `cast function from functionBodyDeclarationNoPortList`() {
        driveCasterTest(
            SystemVerilogParser.FunctionBodyDeclarationNoPortListContext::class,
            """
                function void f;
                    input x;
                endfunction
            """.trimIndent(),
            "SvFunction(f, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], SimpleDescriptor(Unit), 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `cast function from functionBodyDeclarationPortList`() {
        driveCasterTest(
            SystemVerilogParser.FunctionBodyDeclarationPortListContext::class,
            """
                function void f(logic x);
                endfunction
            """.trimIndent(),
            "SvFunction(f, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], SimpleDescriptor(Unit), 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `cast function from functionPrototype`() {
        driveCasterTest(
            SystemVerilogParser.FunctionPrototypeContext::class,
            """
                class c;
                    extern function void f(logic x);
                endclass
            """.trimIndent(),
            "SvFunction(f, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], SimpleDescriptor(Unit), 0)"
        ) { it.findDeclaration("f") }
    }
}
