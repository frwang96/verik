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

internal class BasicClassInterpreterStageTest : BaseTest() {

    @Test
    fun `basic class simple`() {
        driveTest(
            """
                class C
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            """
                SvBasicClass(
                    C, C,
                    [
                        SvFunction(_${'$'}new, *, *, [], REGULAR, 1),
                        SvFunction(_${'$'}init, *, *, [], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent(),
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `basic class with primary constructor parameter`() {
        driveTest(
            """
                class C(x: Int)
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            """
                SvBasicClass(
                    C, C,
                    [
                        SvFunction(_${'$'}new, C, *, [SvValueParameter(x, Int, 1)], REGULAR, 1),
                        SvFunction(_${'$'}init, Unit, *, [SvValueParameter(x, Int, 1)], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `basic class with primary constructor property`() {
        driveTest(
            """
                class C(val x: Int)
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            """
                SvBasicClass(
                    C, C,
                    [
                        KtProperty(x, Int, null, [], 0),
                        SvFunction(_${'$'}new, C, *, [SvValueParameter(x, Int, 1)], REGULAR, 1),
                        SvFunction(_${'$'}init, Unit, *, [SvValueParameter(x, Int, 1)], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `basic class with primary constructor chained`() {
        driveTest(
            """
                open class C
                class D : C()
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            """
                SvBasicClass(
                    D, D,
                    [
                        SvFunction(_${'$'}new, D, *, [], REGULAR, 1),
                        SvFunction(
                            _${'$'}init, Unit,
                            KtBlockExpression(Unit, [KtCallExpression(Unit, _${'$'}init, SuperExpression(C), [], [])]),
                            [], REGULAR, 0
                        )
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }

    @Test
    fun `basic class abstract`() {
        driveTest(
            """
                abstract class C
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            "SvBasicClass(C, C, [SvFunction(_${'$'}init, Unit, *, [], REGULAR, 0)], 1, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `basic class declarations static`() {
        driveTest(
            """
                object O
            """.trimIndent(),
            BasicClassInterpreterStage::class,
            "SvBasicClass(O, O, [], 0, 1)"
        ) { it.findDeclaration("O") }
    }
}
