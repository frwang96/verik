/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeDeclarationCasterTest : BaseTest() {

    @Test
    fun `cast type alias from dataTypeVector`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeVectorContext::class,
            """
                typedef logic t;
            """.trimIndent(),
            "TypeAlias(t, SimpleDescriptor(Boolean))"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `cast enum from dataTypeEnum`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeEnumContext::class,
            """
                typedef enum { A } e;
            """.trimIndent(),
            "Enum(e, [EnumEntry(A)])"
        ) { it.findDeclaration("e") }
    }

    @Test
    fun `cast struct from dataTypeStruct`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeStructContext::class,
            """
                typedef struct {
                    logic x;
                } s;
            """.trimIndent(),
            "Struct(s, [StructEntry(x, SimpleDescriptor(Boolean))])"
        ) { it.findDeclaration("s") }
    }
}
