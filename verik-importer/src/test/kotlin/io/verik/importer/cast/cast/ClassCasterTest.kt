/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ClassCasterTest : BaseTest() {

    @Test
    fun `cast class from classDeclaration`() {
        driveCasterTest(
            SystemVerilogParser.ClassDeclarationContext::class,
            """
                class c;
                endclass
            """.trimIndent(),
            "SvClass(c, [], [], SimpleDescriptor(Class))"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `cast class from classDeclaration with extends`() {
        driveCasterTest(
            SystemVerilogParser.ClassDeclarationContext::class,
            """
                class c extends d;
                endclass
            """.trimIndent(),
            "SvClass(c, [], [], ReferenceDescriptor(Nothing, d, Nothing, []))"
        ) { it.findDeclaration("c") }
    }
}
