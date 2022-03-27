/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class EnumInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret enum simple`() {
        driveElementTest(
            """
                enum class E { A }
            """.trimIndent(),
            EnumInterpreterStage::class,
            "File([Enum(E, E, null, [A]), EnumEntry(A, E, null)])"
        ) { it.files().first() }
    }

    @Test
    fun `interpret enum with property`() {
        driveElementTest(
            """
                enum class E(val value: Ubit<`4`>) { A(u(0x0)) }
            """.trimIndent(),
            EnumInterpreterStage::class,
            """
                File([
                    Enum(E, E, Property(value, Ubit<`4`>, null, 0, 0), [A]),
                    EnumEntry(A, E, ConstantExpression(Ubit<`4`>, 4'b0000))
                ])
            """.trimIndent()
        ) { it.files().first() }
    }
}
