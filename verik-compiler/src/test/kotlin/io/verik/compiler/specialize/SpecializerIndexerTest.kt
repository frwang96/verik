/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SpecializerIndexerTest : BaseTest() {

    @Test
    fun `type resolved`() {
        driveElementTest(
            """
                class C<X: `*`> : Class()
                val x: C<`1`> = C()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, C<`1`>, CallExpression(*), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `primary constructor resolved`() {
        driveElementTest(
            """
                class C<X: `*`> : Class()
                val x = C<`1`>()
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(C<`*`>, C, null, 0, [], [`1`])"
        ) { it.findExpression("x") }
    }
}
