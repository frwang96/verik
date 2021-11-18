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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CoreVkControlTest : BaseTest() {

    @Test
    fun `transform forever`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                fun f() {
                    forever {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "ForeverStatement(Void, KtBlockExpression(*))",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform wait`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = false
                fun f() {
                    wait(posedge(x))
                }
            """.trimIndent()
        )
        assertElementEquals(
            "EventControlExpression(Void, EdgeExpression(Event, *, *))",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `transform delay`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                var x = false
                fun f() {
                    delay(1)
                }
            """.trimIndent()
        )
        assertElementEquals(
            "DelayExpression(Void, ConstantExpression(*))",
            projectContext.findExpression("f")
        )
    }
}
