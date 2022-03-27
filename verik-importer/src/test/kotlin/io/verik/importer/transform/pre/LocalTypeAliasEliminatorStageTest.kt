/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class LocalTypeAliasEliminatorStageTest : BaseTest() {

    @Test
    fun `eliminate local type alias`() {
        driveElementTest(
            """
                class c;
                    typedef logic t;
                    t x;
                endclass
            """.trimIndent(),
            LocalTypeAliasEliminatorStage::class,
            "Property(x, SimpleDescriptor(Boolean), 0, 1)"
        ) { it.findDeclaration("x") }
    }
}
