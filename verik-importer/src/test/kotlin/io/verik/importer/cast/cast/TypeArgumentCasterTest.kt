/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeArgumentCasterTest : BaseTest() {

    @Test
    fun `cast type argument from paramExpressionDataType`() {
        driveCasterTest(
            SystemVerilogParser.ParamExpressionDataTypeContext::class,
            """
                c#(int) x;
            """.trimIndent(),
            """
                Property(
                    x, ReferenceDescriptor(Nothing, c, Nothing, [TypeArgument(null, SimpleDescriptor(Int))]), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast type argument from paramExpressionExpression`() {
        driveCasterTest(
            SystemVerilogParser.ParamExpressionExpressionContext::class,
            """
                c#(1) x;
            """.trimIndent(),
            """
                Property(
                    x, ReferenceDescriptor(Nothing, c, Nothing, [TypeArgument(null, LiteralDescriptor(*))]), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
