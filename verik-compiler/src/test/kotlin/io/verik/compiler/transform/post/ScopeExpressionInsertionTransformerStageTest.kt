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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class ScopeExpressionInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `reference target declaration`() {
        val projectContext = driveTest(
            ScopeExpressionInsertionTransformerStage::class,
            """
                fun f() {
                    ArrayList<Boolean>()
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtCallExpression(
                    ArrayList<Boolean>,
                    _${'$'}new,
                    ScopeExpression(Void, ArrayList<Boolean>),
                    [],
                    [Boolean]
                )
            """.trimIndent(),
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `reference element parent file`() {
        val projectContext = driveTest(
            ScopeExpressionInsertionTransformerStage::class,
            """
                var x = false
                class M : Module() {
                    fun f() {
                        x
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, test_pkg))",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `reference property parent basic class`() {
        val projectContext = driveTest(
            ScopeExpressionInsertionTransformerStage::class,
            """
                object O {
                    var x = false
                }
                class M : Module() {
                    fun f() {
                        O.x
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ReferenceExpression(Boolean, x, ScopeExpression(Void, O))",
            projectContext.findExpression("f")
        )
    }
}
