/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PackageCasterTest : BaseTest() {

    @Test
    fun `cast package from packageDeclaration`() {
        driveCasterTest(
            SystemVerilogParser.PackageDeclarationContext::class,
            """
                package p;
                    logic x;
                endpackage
            """.trimIndent(),
            "SvPackage(p, [Property(x, SimpleDescriptor(Boolean), 0, 1)])"
        ) { it.findDeclaration("p") }
    }
}
