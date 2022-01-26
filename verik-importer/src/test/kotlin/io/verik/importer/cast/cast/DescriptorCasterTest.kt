/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            "Property(x, SimpleDescriptor(Boolean))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from dataTypeTypeIdentifier`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeTypeIdentifierContext::class,
            """
                t x;
            """.trimIndent(),
            "Property(x, ReferenceDescriptor(Nothing, t))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from dataTypeInteger`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeIntegerContext::class,
            """
                int x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Int))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from implicitDataType`() {
        driveCasterTest(
            SystemVerilogParser.ImplicitDataTypeContext::class,
            """
                x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from packedDimensionRange single`() {
        driveCasterTest(
            SystemVerilogParser.PackedDimensionRangeContext::class,
            """
                logic [1:0] x;
            """.trimIndent(),
            "Property(x, BitDescriptor(Nothing, LiteralExpression(1), LiteralExpression(0), 0))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast descriptor from packedDimensionRange multiple`() {
        driveCasterTest(
            SystemVerilogParser.PackedDimensionRangeContext::class,
            """
                logic [1:0][1:0] x;
            """.trimIndent(),
            "Property(x, PackedDescriptor(Nothing, BitDescriptor(*), LiteralExpression(1), LiteralExpression(0)))"
        ) { it.findDeclaration("x") }
    }
}
