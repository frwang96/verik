/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class StructUnionInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret struct`() {
        driveElementTest(
            """
                class S(var x: Boolean): Struct()
            """.trimIndent(),
            StructUnionInterpreterStage::class,
            "Struct(S, S, [Property(x, Boolean, null, 1, 0)])"
        ) { it.findDeclaration("S") }
    }

    @Test
    fun `interpret union`() {
        driveElementTest(
            """
                class U(var x: Boolean, var y: Ubit<`1`>): Union()
            """.trimIndent(),
            StructUnionInterpreterStage::class,
            "Union(U, U, [Property(x, Boolean, null, 1, 0), Property(y, Ubit<`1`>, null, 1, 0)])"
        ) { it.findDeclaration("U") }
    }
}
