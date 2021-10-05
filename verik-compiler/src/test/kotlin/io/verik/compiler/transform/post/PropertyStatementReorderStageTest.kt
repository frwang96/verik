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

import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class PropertyStatementReorderStageTest : BaseTest() {

    @Test
    fun `reorder property statement with initializer`() {
        val projectContext = driveTest(
            PropertyStatementReorderStage::class,
            """
                fun f() {
                    println()
                    val x = false
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBlockExpression(
                    Unit,
                    [
                        SvPropertyStatement(Unit, SvProperty(x, Boolean, null, false)),
                        KtCallExpression(*),
                        SvBinaryExpression(Unit, KtReferenceExpression(Boolean, x, null), ConstantExpression(*), ASSIGN)
                    ]
                )
            """.trimIndent(),
            (projectContext.findDeclaration("f") as ESvFunction).body!!
        )
    }

    @Test
    fun `reorder property statement without initializer`() {
        val projectContext = driveTest(
            PropertyStatementReorderStage::class,
            """
                fun f() {
                    println()
                    val x: Boolean = nc()
                }
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBlockExpression(
                    Unit,
                    [SvPropertyStatement(Unit, SvProperty(x, Boolean, null, false)), KtCallExpression(*)]
                )
            """.trimIndent(),
            (projectContext.findDeclaration("f") as ESvFunction).body!!
        )
    }
}