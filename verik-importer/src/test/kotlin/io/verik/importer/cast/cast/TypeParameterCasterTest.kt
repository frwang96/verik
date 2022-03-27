/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeParameterCasterTest : BaseTest() {

    @Test
    fun `cast type parameter from parameterPortDeclarationDataType`() {
        driveCasterTest(
            SystemVerilogParser.ParameterPortDeclarationDataTypeContext::class,
            """
                class c #(int N);
                endclass
            """.trimIndent(),
            "SvClass(c, [], [TypeParameter(N, null, 1)], SimpleDescriptor(Class))"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `cast type parameter from parameterPortDeclarationType`() {
        driveCasterTest(
            SystemVerilogParser.ParameterPortDeclarationTypeContext::class,
            """
                class c #(type T = int);
                endclass
            """.trimIndent(),
            "SvClass(c, [], [TypeParameter(T, SimpleDescriptor(Int), 0)], SimpleDescriptor(Class))"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `cast type parameter from parameterPortDeclarationType multiple`() {
        driveCasterTest(
            SystemVerilogParser.ParameterPortDeclarationTypeContext::class,
            """
                module m #(type T, type U);
                endmodule
            """.trimIndent(),
            "Module(m, [], [TypeParameter(T, null, 0), TypeParameter(U, null, 0)], [])"
        ) { it.findDeclaration("m") }
    }
}
