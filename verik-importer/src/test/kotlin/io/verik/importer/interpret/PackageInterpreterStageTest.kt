/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class PackageInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret root package`() {
        driveElementTest(
            """
                logic [1:0] x;
            """.trimIndent(),
            PackageInterpreterStage::class,
            "Project([KtPackage(imported, [KtFile(test.kt, [Property(*)])])])"
        ) { it }
    }

    @Test
    fun `interpret regular package`() {
        driveElementTest(
            """
                package p;
                    logic [1:0] x;
                endpackage
            """.trimIndent(),
            PackageInterpreterStage::class,
            "Project([KtPackage(imported.p, [KtFile(test.kt, [Property(*)])])])"
        ) { it }
    }
}
