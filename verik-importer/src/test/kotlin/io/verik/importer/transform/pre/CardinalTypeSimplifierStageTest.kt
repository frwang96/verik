/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CardinalTypeSimplifierStageTest : BaseTest() {

    @Test
    fun `resolve bitDescriptor`() {
        driveElementTest(
            """
                logic [1:0] x;
            """.trimIndent(),
            CardinalTypeSimplifierStage::class,
            "Property(x, BitDescriptor(Ubit<`2`>, *, *, *), 0, 1)"
        ) { it.findDeclaration("x") }
    }
}
