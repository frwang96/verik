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

package io.verik.compiler.target.declaration

import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.util.BaseTest
import org.junit.jupiter.api.Test

internal class TargetClassTest : BaseTest() {

    @Test
    fun `serialize type Boolean`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: Boolean = nc()
            """.trimIndent()
        )
        val expected = """
            logic x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize type Ubit`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: Ubit<`8`> = nc()
            """.trimIndent()
        )
        val expected = """
            logic [7:0] x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize type Sbit`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: Sbit<`8`> = nc()
            """.trimIndent()
        )
        val expected = """
            logic signed [7:0] x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize type Packed`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: Packed<`8`, Boolean> = nc()
            """.trimIndent()
        )
        val expected = """
            logic [7:0] x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize type Unpacked`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: Unpacked<`8`, Boolean> = nc()
            """.trimIndent()
        )
        val expected = """
            logic x [7:0];
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }

    @Test
    fun `serialize type ArrayList`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                var x: ArrayList<Boolean> = nc()
            """.trimIndent()
        )
        val expected = """
            verik_pkg::ArrayList#(logic) x;
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.basicPackageSourceTextFiles[0]
        )
    }
}
