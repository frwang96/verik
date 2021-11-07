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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

internal class BasicClassInterpreterStageTest : BaseTest() {

    @Test
    fun `basic class simple`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                class C
            """.trimIndent()
        )
        assertElementEquals(
            """
                SvBasicClass(
                    C,
                    [
                        SvFunction(_${'$'}new, *, *, 1, REGULAR, []),
                        SvFunction(_${'$'}init, *, *, 0, REGULAR, [])
                    ],
                    0, 0
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `basic class with primary constructor parameter`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                class C(x: Int)
            """.trimIndent()
        )
        assertElementEquals(
            """
                SvBasicClass(
                    C,
                    [
                        SvFunction(_${'$'}new, C, *, 1, REGULAR, [SvValueParameter(x, Int)]),
                        SvFunction(_${'$'}init, Unit, *, 0, REGULAR, [SvValueParameter(x, Int)])
                    ],
                    0, 0
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `basic class with primary constructor property`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                class C(val x: Int)
            """.trimIndent()
        )
        assertElementEquals(
            """
                SvBasicClass(
                    C,
                    [
                        KtProperty(x, Int, null, [], 0),
                        SvFunction(_${'$'}new, C, *, 1, REGULAR, [SvValueParameter(x, Int)]),
                        SvFunction(_${'$'}init, Unit, *, 0, REGULAR, [SvValueParameter(x, Int)])
                    ],
                    0, 0
                )
            """.trimIndent(),
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `basic class with primary constructor chained`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                open class C
                class D : C()
            """.trimIndent()
        )
        assertElementEquals(
            """
                SvBasicClass(
                    D,
                    [
                        SvFunction(_${'$'}new, D, *, 1, REGULAR, []),
                        SvFunction(
                            _${'$'}init, Unit,
                            KtBlockExpression(Unit, [KtCallExpression(Unit, _${'$'}init, SuperExpression(C), [], [])]),
                            0, REGULAR, []
                        )
                    ],
                    0, 0
                )
            """.trimIndent(),
            projectContext.findDeclaration("D")
        )
    }

    @Test
    fun `basic class abstract`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                abstract class C
            """.trimIndent()
        )
        assertElementEquals(
            "SvBasicClass(C, [SvFunction(_${'$'}init, Unit, *, 0, REGULAR, [])], 1, 0)",
            projectContext.findDeclaration("C")
        )
    }

    @Test
    fun `basic class declarations static`() {
        val projectContext = driveTest(
            BasicClassInterpreterStage::class,
            """
                object O
            """.trimIndent()
        )
        assertElementEquals(
            "SvBasicClass(O, [], 0, 1)",
            projectContext.findDeclaration("O")
        )
    }
}
