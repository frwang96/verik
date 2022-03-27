/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PropertyCasterTest : BaseTest() {

    @Test
    fun `cast property from classProperty`() {
        driveCasterTest(
            SystemVerilogParser.ClassPropertyContext::class,
            """
                class c;
                    static logic x;
                endclass
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 1, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast property from dataDeclarationData single`() {
        driveCasterTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                const logic x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast property from dataDeclarationData multiple`() {
        driveCasterTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                logic x, y;
            """.trimIndent(),
            """
                Project([
                    Property(x, SimpleDescriptor(Boolean), 0, 1),
                    Property(y, SimpleDescriptor(Boolean), 0, 1)
                ])
            """.trimIndent()
        ) { it }
    }
}
