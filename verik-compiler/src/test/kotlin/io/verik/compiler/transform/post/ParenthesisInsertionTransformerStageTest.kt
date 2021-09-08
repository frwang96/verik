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
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class ParenthesisInsertionTransformerStageTest : BaseTest() {

    @Test
    fun `binary expression left`() {
        val projectContext = driveTest(
            ParenthesisInsertionTransformerStage::class,
            """
                var x = 0
                var y = (x + 1) * x
            """.trimIndent()
        )
        assertElementEquals(
            "SvProperty(y, Int, SvBinaryExpression(Int, MUL, ParenthesizedExpression(*), SvReferenceExpression(*)))",
            projectContext.findDeclaration("y")
        )
    }

    @Test
    fun `binary expression right`() {
        val projectContext = driveTest(
            ParenthesisInsertionTransformerStage::class,
            """
                var x = 0
                var y = x + (1 + x)
            """.trimIndent()
        )
        assertElementEquals(
            "SvProperty(y, Int, SvBinaryExpression(Int, PLUS, SvReferenceExpression(*), ParenthesizedExpression(*)))",
            projectContext.findDeclaration("y")
        )
    }

    @Test
    fun `event control expression`() {
        val projectContext = driveTest(
            ParenthesisInsertionTransformerStage::class,
            """
                class M : Module() {
                    @Seq
                    fun f() {
                        on(posedge(false)) {}
                    }
                }
            """.trimIndent()
        )
        assertElementEquals(
            "AlwaysSeqBlock(f, EventControlExpression(Unit, ParenthesizedExpression(*)), *)",
            projectContext.findDeclaration("f")
        )
    }
}
