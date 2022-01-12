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

package io.verik.compiler.reorder

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class PropertyStatementReordererStageTest : BaseTest() {

    @Test
    fun `reorder property statement with initializer`() {
        driveElementTest(
            """
                fun f() {
                    println()
                    val x = false
                }
            """.trimIndent(),
            PropertyStatementReordererStage::class,
            """
                [
                    PropertyStatement(Unit, SvProperty(x, Boolean, null, 0, 0)),
                    CallExpression(*),
                    SvBinaryExpression(Unit, ReferenceExpression(Boolean, x, null), ConstantExpression(*), ASSIGN)
                ]
            """.trimIndent()
        ) { it.findStatements("f") }
    }

    @Test
    fun `reorder property statement without initializer`() {
        driveElementTest(
            """
                fun f() {
                    println()
                    val x: Boolean = nc()
                }
            """.trimIndent(),
            PropertyStatementReordererStage::class,
            "[PropertyStatement(Unit, SvProperty(x, Boolean, null, 0, 0)), CallExpression(*)]"
        ) { it.findStatements("f") }
    }
}
