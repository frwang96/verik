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

internal class ClassInterpreterStageTest : BaseTest() {

    @Test
    fun `class simple`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    C, C, [],
                    [
                        SvFunction(__new, *, *, [], REGULAR, 1),
                        SvFunction(__init, *, *, [], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent(),
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor parameter`() {
        driveElementTest(
            """
                class C(x: Int)
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    C, C, [],
                    [
                        SvFunction(__new, C, *, [SvValueParameter(x, Int, null, 1)], REGULAR, 1),
                        SvFunction(__init, Unit, *, [SvValueParameter(x, Int, null, 1)], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor property`() {
        driveElementTest(
            """
                class C(val x: Int)
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    C, C, [],
                    [
                        Property(x, Int, null, 0, 0),
                        SvFunction(__new, C, *, [SvValueParameter(x, Int, null, 1)], REGULAR, 1),
                        SvFunction(__init, Unit, *, [SvValueParameter(x, Int, null, 1)], REGULAR, 0)
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class with primary constructor chained`() {
        driveElementTest(
            """
                open class C
                class D : C()
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    D, D, [],
                    [
                        SvFunction(__new, D, *, [], REGULAR, 1),
                        SvFunction(
                            __init, Unit,
                            BlockExpression(Unit, [CallExpression(Unit, __init, SuperExpression(C), [], [])]),
                            [], REGULAR, 0
                        )
                    ],
                    0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }

    @Test
    fun `class abstract`() {
        driveElementTest(
            """
                abstract class C
            """.trimIndent(),
            ClassInterpreterStage::class,
            "SvClass(C, C, [], [SvFunction(__init, Unit, *, [], REGULAR, 0)], 1, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class declarations static`() {
        driveElementTest(
            """
                object O
            """.trimIndent(),
            ClassInterpreterStage::class,
            "SvClass(O, O, [], [], 0, 1)"
        ) { it.findDeclaration("O") }
    }
}
