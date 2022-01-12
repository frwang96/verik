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

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ScopeExpressionInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `reference target declaration`() {
        driveElementTest(
            """
                fun f() {
                    ArrayList<Boolean>()
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            """
                CallExpression(
                    ArrayList<Boolean>,
                    _${'$'}new,
                    ScopeExpression(Void, ArrayList<Boolean>),
                    [],
                    [Boolean]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference package property`() {
        driveElementTest(
            """
                var x = false
                class M : Module() {
                    fun f() {
                        x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, test_pkg))"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference package class`() {
        driveElementTest(
            """
                object O {
                    var x = false
                }
                class M : Module() {
                    fun f() {
                        O.x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, O))"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference module`() {
        driveElementTest(
            """
                @SimTop
                object M : Module() {
                    val x: Boolean = nc()
                }
                class N : Module() {
                    fun f() {
                        M.x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, ReferenceExpression(M, M, ReferenceExpression(Void, ${'$'}root, null)))"
        ) { it.findExpression("f") }
    }

    @Test
    fun `reference module parent`() {
        driveElementTest(
            """
                @SimTop
                object M : Module() {
                    @Suppress("MemberVisibilityCanBePrivate")
                    val x: Boolean = nc()
                    fun f() {
                        x
                    }
                }
            """.trimIndent(),
            ScopeExpressionInsertionTransformerStage::class,
            "ReferenceExpression(Boolean, x, null)"
        ) { it.findExpression("f") }
    }
}
