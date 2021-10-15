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

package io.verik.compiler.transform.mid

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class AssignmentTransformerStageTest : BaseTest() {

    @Test
    fun `transform assign`() {
        val projectContext = driveTest(
            AssignmentTransformerStage::class,
            """
                var x = false
                fun f() {
                    x = true
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Unit, KtReferenceExpression(*), ConstantExpression(*), ASSIGN)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform arrow assign seq block`() {
        val projectContext = driveTest(
            AssignmentTransformerStage::class,
            """
                class M : Module() {
                    private var x = false
                    @Seq
                    fun f() {
                        on(posedge(false)) {
                            x = true
                        }
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Unit, KtReferenceExpression(*), ConstantExpression(*), ARROW_ASSIGN)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform arrow assign clocking block`() {
        val projectContext = driveTest(
            AssignmentTransformerStage::class,
            """
                class CB(override val event: Event, @In var x: Boolean) : ClockingBlock()
                class M : Module() {
                    private var x = false
                    @Make
                    private var cb = CB(posedge(false), x)
                    @Run
                    fun f() {
                        cb.x = true
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "SvBinaryExpression(Unit, KtReferenceExpression(*), ConstantExpression(*), ARROW_ASSIGN)",
            projectContext.findExpression("f")
        )
    }
}
