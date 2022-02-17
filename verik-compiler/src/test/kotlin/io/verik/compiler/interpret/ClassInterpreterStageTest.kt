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
                class C : Class()
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                SvClass(
                    C, C, Class, [],
                    [SecondaryConstructor(C, C, BlockExpression(*), [], CallExpression(Class, Class, null, [], []))],
                    0
                )
            """.trimIndent(),
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `class declarations static`() {
        driveElementTest(
            """
                object O : Class()
            """.trimIndent(),
            ClassInterpreterStage::class,
            "SvClass(O, O, Class, [], [], 1)"
        ) { it.findDeclaration("O") }
    }
}
