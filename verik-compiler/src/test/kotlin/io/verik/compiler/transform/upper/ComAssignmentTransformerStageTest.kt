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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ComAssignmentTransformerStageTest : BaseTest() {

    @Test
    fun `com assignment illegal not mutable`() {
        driveMessageTest(
            """
                fun f(): Boolean { return false }
                class M : Module() {
                    @Com val x = f()
                }
            """.trimIndent(),
            true,
            "Combinational assignment must be declared as var"
        )
    }

    @Test
    fun `com assignment illegal no initializer`() {
        driveMessageTest(
            """
                fun f(): Boolean {
                    return false
                }
                class M : Module() {
                    @Com var x: Boolean = nc()
                }
            """.trimIndent(),
            true,
            "Initializer expected for combinational assignment"
        )
    }

    @Test
    fun `com assignment legal`() {
        driveElementTest(
            """
                class M : Module() {
                    @Com
                    var x = false
                }
            """.trimIndent(),
            ComAssignmentTransformerStage::class,
            """
                Module(
                    M, M,
                    [
                        SvProperty(x, Boolean, null, 1, 1, null),
                        AlwaysComBlock(
                            <tmp>,
                            BlockExpression(
                                Unit,
                                [KtBinaryExpression(*, ReferenceExpression(*, x, null), ConstantExpression(*), EQ)]
                            )
                        )
                    ],
                    []
                )
            """.trimIndent()
        ) { it.findDeclaration("M") }
    }
}
