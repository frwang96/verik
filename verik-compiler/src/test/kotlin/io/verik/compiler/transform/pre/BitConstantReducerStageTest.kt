/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class BitConstantReducerStageTest : BaseTest() {

    @Test
    fun `constant decimal`() {
        driveElementTest(
            """
                var x = u(255)
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`8`>, 8'hff)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant hexadecimal`() {
        driveElementTest(
            """
                var x = u(0x00_0000_00ff)
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`40`>, 40'h00_0000_00ff)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned decimal`() {
        driveElementTest(
            """
                var x = u("4'd3")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`4`>, 4'b0011)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned binary`() {
        driveElementTest(
            """
                var x = u("9'b100000011")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`9`>, 9'h103)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned binary zero extension`() {
        driveElementTest(
            """
                var x = u("4'b1")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`4`>, 4'b0001)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned binary unknown floating`() {
        driveElementTest(
            """
                var x = u("5'b100xz")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`5`>, 5'b1_00xz)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned hexadecimal`() {
        driveElementTest(
            """
                var x = u("10'h3ff")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`10`>, 10'h3ff)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned hexadecimal insufficient width`() {
        driveMessageTest(
            """
                var x = u("1'b11")
            """.trimIndent(),
            true,
            "Bit constant is insufficiently wide: 1'b11"
        )
    }

    @Test
    fun `constant string signed binary`() {
        driveElementTest(
            """
                var x = s("9'b100000011")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Sbit<`9`>, 9'sh103)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string signed hexadecimal`() {
        driveElementTest(
            """
                var x = s("10'h3ff")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Sbit<`10`>, 10'sh3ff)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string signed hexadecimal sign extension`() {
        driveElementTest(
            """
                var x = s("8'hf")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Sbit<`8`>, 8'shff)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string invalid`() {
        driveMessageTest(
            """
                var x = u("12'hxyz")
            """.trimIndent(),
            true,
            "Error parsing bit constant: 12'hxyz"
        )
    }
}
