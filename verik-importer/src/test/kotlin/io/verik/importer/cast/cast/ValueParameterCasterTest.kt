/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ValueParameterCasterTest : BaseTest() {

    @Test
    fun `cast value parameter from tfPortItem`() {
        driveCasterTest(
            SystemVerilogParser.TfPortItemContext::class,
            """
                function void f(int x);
                endfunction
            """.trimIndent(),
            "SvValueParameter(x, SimpleDescriptor(Int), 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast value parameter from tfPortDeclaration multiple`() {
        driveCasterTest(
            SystemVerilogParser.TfPortDeclarationContext::class,
            """
                function void f;
                    input x, y = 1'b1;
                endfunction
            """.trimIndent(),
            """
                SvFunction(
                    f, [
                        SvValueParameter(x, SimpleDescriptor(Boolean), 0),
                        SvValueParameter(y, SimpleDescriptor(Boolean), 1)
                    ], SimpleDescriptor(Unit), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }
}
