/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class DescriptorCasterTest : BaseTest() {

    @Test
    fun `cast descriptor from dataTypeVector`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeVectorContext::class,
            """
                logic x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from dataTypeTypeIdentifier`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeTypeIdentifierContext::class,
            """
                t x;
            """.trimIndent(),
            "Property(x, ReferenceDescriptor(Nothing, t, Nothing, []), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from dataTypeInteger`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeIntegerContext::class,
            """
                int x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Int), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from implicitDataType`() {
        driveCasterTest(
            SystemVerilogParser.ImplicitDataTypeContext::class,
            """
                x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from unpackedDimensionRange`() {
        driveCasterTest(
            SystemVerilogParser.UnpackedDimensionRangeContext::class,
            """
                int x [7:0];
            """.trimIndent(),
            """
                Property(
                    x, RangeDimensionDescriptor(
                        Nothing, SimpleDescriptor(Int), LiteralDescriptor(*), LiteralDescriptor(*), 0
                    ), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from unpackedDimensionExpression`() {
        driveCasterTest(
            SystemVerilogParser.UnpackedDimensionExpressionContext::class,
            """
                int x [8];
            """.trimIndent(),
            "Property(x, IndexDimensionDescriptor(Nothing, SimpleDescriptor(Int), LiteralDescriptor(Nothing, 8)), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from packedDimensionRange single`() {
        driveCasterTest(
            SystemVerilogParser.PackedDimensionRangeContext::class,
            """
                logic [1:0] x;
            """.trimIndent(),
            "Property(x, BitDescriptor(Nothing, LiteralDescriptor(*), LiteralDescriptor(*), 0), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from packedDimensionRange multiple`() {
        driveCasterTest(
            SystemVerilogParser.PackedDimensionRangeContext::class,
            """
                logic [1:0][1:0] x;
            """.trimIndent(),
            """
                Property(
                    x,
                    RangeDimensionDescriptor(Nothing, BitDescriptor(*), LiteralDescriptor(*), LiteralDescriptor(*), 1),
                    0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from associative dimension`() {
        driveCasterTest(
            SystemVerilogParser.AssociativeDimensionContext::class,
            """
                logic x [int];
            """.trimIndent(),
            "Property(x, IndexDimensionDescriptor(Nothing, SimpleDescriptor(Boolean), SimpleDescriptor(Int)), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from queueDimension`() {
        driveCasterTest(
            SystemVerilogParser.QueueDimensionContext::class,
            """
                logic x [$];
            """.trimIndent(),
            "Property(x, ArrayDimensionDescriptor(Nothing, SimpleDescriptor(*), 1), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from unsizedDimension`() {
        driveCasterTest(
            SystemVerilogParser.UnsizedDimensionContext::class,
            """
                logic x [];
            """.trimIndent(),
            "Property(x, ArrayDimensionDescriptor(Nothing, SimpleDescriptor(*), 0), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from constantPrimaryParameter`() {
        driveCasterTest(
            SystemVerilogParser.ConstantPrimaryParameterContext::class,
            """
                logic [N:0] x;
            """.trimIndent(),
            """
                Property(
                    x, BitDescriptor(Nothing, ReferenceDescriptor(Nothing, N, Nothing, []), LiteralDescriptor(*), 0),
                    0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from primaryHierarchical`() {
        driveCasterTest(
            SystemVerilogParser.PrimaryHierarchicalContext::class,
            """
                c#(d) x;
            """.trimIndent(),
            """
                Property(
                    x, ReferenceDescriptor(Nothing, c, Nothing,
                    [TypeArgument(null, ReferenceDescriptor(Nothing, d, Nothing, []))]), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
