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

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class ConstructorDesugarTransformerStageTest : BaseTest() {

    @Test
    fun `desugar primary constructor simple`() {
        val projectContext = driveTest(
            ConstructorDesugarTransformerStage::class,
            """
                class C
            """.trimIndent()
        )
        assertElementEquals(
            "KtBasicClass(C, [KtConstructor(C, KtBlockExpression(Unit, []), [], [], null)], [], [], false, null, null)",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `desugar primary constructor with property`() {
        val projectContext = driveTest(
            ConstructorDesugarTransformerStage::class,
            """
                class C(val x: Int)
            """.trimIndent()
        )
        assertElementEquals(
            """
                KtBasicClass(
                    C,
                    [
                        KtProperty(x, Int, null, []),
                        KtConstructor(
                            C,
                            KtBlockExpression(*),
                            [KtValueParameter(x, Int, [], false)],
                            [],
                            null
                        )
                    ],
                    [],
                    [],
                    false,
                    null,
                    null
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
        val constructor = (projectContext.findDeclaration("C") as EKtBasicClass).declarations[1]
        val statements = ((constructor as EKtConstructor).body!! as EKtBlockExpression).statements
        assertElementEquals(
            """
                [KtBinaryExpression(
                    Unit,
                    KtReferenceExpression(Int, x, ThisExpression(C)),
                    KtReferenceExpression(Int, x, null),
                    EQ
                )]
            """.trimIndent(),
            statements
        )
    }
}
