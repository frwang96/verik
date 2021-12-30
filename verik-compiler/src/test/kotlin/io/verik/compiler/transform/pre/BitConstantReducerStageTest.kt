/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            "ConstantExpression(Ubit<`4`>, 4'h3)"
        ) { it.findExpression("x") }
    }

    @Test
    fun `constant string unsigned binary`() {
        driveElementTest(
            """
                var x = u("4'b0011")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Ubit<`4`>, 4'h3)"
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
    fun `constant string signed binary`() {
        driveElementTest(
            """
                var x = s("4'b0011")
            """.trimIndent(),
            BitConstantReducerStage::class,
            "ConstantExpression(Sbit<`4`>, 4'sh3)"
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
    fun `constant string invalid`() {
        driveMessageTest(
            """
                var x = u("12'hxyz")
            """.trimIndent(),
            true,
            "Error parsing bit constant: 12'hxyz"
        )
    }

    @Test
    fun `constant string insufficient width`() {
        driveMessageTest(
            """
                var x = u("1'hf")
            """.trimIndent(),
            true,
            "Bit constant is insufficiently wide: 1'hf"
        )
    }
}
