/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CardinalTypeEvaluatorSubstageTest : BaseTest() {

    @Test
    fun `function true`() {
        driveElementTest(
            """
                var x: Ubit<TRUE> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`1`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function false`() {
        driveElementTest(
            """
                var x: Ubit<FALSE> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`0`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function not`() {
        driveElementTest(
            """
                var x: Ubit<NOT<`0`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`1`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function and`() {
        driveElementTest(
            """
                var x: Ubit<AND<`1`, `1`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`1`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function or`() {
        driveElementTest(
            """
                var x: Ubit<OR<`1`, `1`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`1`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function if`() {
        driveElementTest(
            """
                var x: Ubit<IF<`1`, `2`, `3`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`2`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function add`() {
        driveElementTest(
            """
                var x: Ubit<ADD<`1`, `1`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`2`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function sub`() {
        driveElementTest(
            """
                var x: Ubit<SUB<`3`, `2`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`1`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function mul`() {
        driveElementTest(
            """
                var x: Ubit<MUL<`3`, `2`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`6`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function div`() {
        driveElementTest(
            """
                var x: Ubit<DIV<`6`, `2`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`3`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function log`() {
        driveElementTest(
            """
                var x: Ubit<LOG<`4`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`2`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function width`() {
        driveElementTest(
            """
                var x: Ubit<WIDTH<`4`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`3`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function exp`() {
        driveElementTest(
            """
                var x: Ubit<EXP<`2`>> = nc()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, Ubit<`4`>, [], *, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `function exp illegal`() {
        driveMessageTest(
            """
                var x: Ubit<EXP<`32`>> = nc()
            """.trimIndent(),
            true,
            "Cardinal type out of range"
        )
    }
}
