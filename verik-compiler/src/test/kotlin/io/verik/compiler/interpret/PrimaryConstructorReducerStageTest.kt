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

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class PrimaryConstructorReducerStageTest : BaseTest() {

    @Test
    fun `reduce primary constructor simple`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            PrimaryConstructorReducerStage::class,
            """
                KtClass(
                    C, C, Any, [],
                    [SecondaryConstructor(C, C, BlockExpression(Unit, []), [], null)],
                    null, 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `reduce primary constructor with property`() {
        val blockExpression = """
            BlockExpression(
                Unit,
                [KtBinaryExpression(
                    Unit,
                    ReferenceExpression(Int, x, ThisExpression(C)),
                    ReferenceExpression(Int, x, null),
                    EQ
                )]
            )
        """.trimIndent()
        driveElementTest(
            """
                class C(val x: Int)
            """.trimIndent(),
            PrimaryConstructorReducerStage::class,
            """
                KtClass(
                    C, C, Any, [], [
                        Property(x, Int, null, 0, 0),
                        SecondaryConstructor(C, C, $blockExpression, [KtValueParameter(*)], null)
                    ], null, 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }
}
