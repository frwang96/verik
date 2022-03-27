/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class OptionalReducerSubstageTest : BaseTest() {

    @Test
    fun `reduce true`() {
        driveElementTest(
            """
                class M : Module()
                val m = optional(true) { M() }
            """.trimIndent(),
            SpecializerStage::class,
            "Property(m, M, CallExpression(M, M, null, 0, [], []), 0, 0)"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `reduce false`() {
        driveElementTest(
            """
                class M : Module()
                val m = optional(false) { M() }
            """.trimIndent(),
            SpecializerStage::class,
            "Property(m, Nothing, ConstantExpression(Nothing, null), 0, 0)"
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `illegal not direct assignment`() {
        driveMessageTest(
            """
                class M : Module()
                var m: M? = nc()
                fun f() {
                    m = optional(true) { M() }
                }
            """.trimIndent(),
            true,
            "Optional must be directly assigned to a property"
        )
    }

    @Test
    fun `illegal not val`() {
        driveMessageTest(
            """
                class M : Module()
                var m = optional(true) { M() }
            """.trimIndent(),
            true,
            "Property assigned as optional must be declared as val"
        )
    }
}
