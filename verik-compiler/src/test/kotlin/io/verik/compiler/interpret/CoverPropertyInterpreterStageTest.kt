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

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CoverPropertyInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret cover point`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverPoint(cp, ReferenceExpression(Boolean, x, null, 0), [])"
        ) { it.findDeclaration("cp") }
    }

    @Test
    fun `interpret cover cross`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp, cp)
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverCross(cc, [cp, cp], [])"
        ) { it.findDeclaration("cc") }
    }

    @Test
    fun `interpret cover cross illegal`() {
        driveMessageTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp)
                }
            """.trimIndent(),
            true,
            "Cover cross should cross at least two cover points"
        )
    }

    @Test
    fun `interpret cover bin`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x) {
                        bin("b", "{1'b0}")
                    }
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverBin(b, StringTemplateExpression(String, [{1'b0}]), 0, 0)"
        ) { it.findDeclaration("b") }
    }

    @Test
    fun `interpret cover bins`() {
        driveElementTest(
            """
                class CG(@In var x: Boolean): CoverGroup() {
                    @Cover
                    val cp = cp(x)
                    @Cover
                    val cc = cc(cp, cp) {
                        bins("b", "binsof(cp)")
                    }
                }
            """.trimIndent(),
            CoverPropertyInterpreterStage::class,
            "CoverBin(b, StringTemplateExpression(String, [binsof(cp)]), 0, 1)"
        ) { it.findDeclaration("b") }
    }
}
