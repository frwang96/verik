/*
 * Copyright (c) 2022 Francis Wang
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

internal class ConstructorInterpreterStageTest : BaseTest() {

    @Test
    fun `constructor with parameter`() {
        driveElementTest(
            """
                class C(x: Int) : Class()
            """.trimIndent(),
            ConstructorInterpreterStage::class,
            """
                SvClass(
                    C, C, Class, [],
                    [SvConstructor(C, BlockExpression(*), [SvValueParameter(x, Int, null, 1)])],
                    0
                )
            """.trimIndent()
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `constructor with super type call expression`() {
        driveElementTest(
            """
                open class C : Class()
                class D : C()
            """.trimIndent(),
            ConstructorInterpreterStage::class,
            """
                SvClass(
                    D, D, C, [],
                    [SvConstructor(
                        D, BlockExpression(Unit, [CallExpression(Unit, new, SuperExpression(C), [], [])]), []
                    )],
                    0
                )
            """.trimIndent()
        ) { it.findDeclaration("D") }
    }
}
