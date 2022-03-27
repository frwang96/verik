/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeReferenceForwarderStageTest : BaseTest() {

    @Test
    fun `forward type not parameterized`() {
        driveElementTest(
            """
                class C : Class()
                val x = C()
            """.trimIndent(),
            TypeReferenceForwarderStage::class,
            "Property(x, C, *, 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `forward type parameterized`() {
        driveElementTest(
            """
                class C<N : `*`> : Class()
                val x = C<`1`>()
            """.trimIndent(),
            TypeReferenceForwarderStage::class,
            "Property(x, C_N_1, *, 0, 0)"
        ) { it.findDeclaration("x") }
    }
}
